package pt.isep.desofs.vendnet.application.service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.api.dto.AuthResponse;
import pt.isep.desofs.vendnet.api.dto.LoginRequest;
import pt.isep.desofs.vendnet.api.dto.MfaVerifyRequest;
import pt.isep.desofs.vendnet.api.dto.RegisterRequest;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.domain.exception.AccountLockedException;
import pt.isep.desofs.vendnet.domain.exception.DisabledException;
import pt.isep.desofs.vendnet.domain.exception.UnauthorizedException;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCK_DURATION_MINUTES = 30;
	private static final int LOCK_WINDOW_MINUTES = 15;
	private static final int TOTP_PERIOD = 30;
	private static final int TOTP_DIGITS = 6;
	private static final String TOTP_ALGORITHM = "HmacSHA1";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@PreAuthorize("permitAll()")
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		LocalDateTime now = LocalDateTime.now();

		User user =
				User.builder()
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.name(request.getName())
						.role(Role.ROLE_CUSTOMER)
						.accountStatus(AccountStatus.ACTIVE)
						.createdAt(now)
						.updatedAt(now)
						.build();

		userRepository.save(user);

		String token = jwtService.generateToken(user.getEmail());

		return AuthResponse.builder()
				.token(token)
				.email(user.getEmail())
				.name(user.getName())
				.role(user.getRole().name())
				.mfaRequired(false)
				.build();
	}

	@PreAuthorize("permitAll()")
	public AuthResponse login(LoginRequest request) {
		User user =
				userRepository
						.findByEmail(request.getEmail())
						.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

		if (isAccountSuspended(user)) {
			throw new DisabledException("Account is suspended");
		}

		if (isAccountLocked(user)) {
			throw new AccountLockedException(
					"Account is temporarily locked. Try again in "
							+ LOCK_DURATION_MINUTES
							+ " minutes.");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			handleFailedLogin(user);
			throw new UnauthorizedException("Invalid email or password");
		}

		resetLockout(user);

		if (user.getRole() == Role.ROLE_ADMINISTRATOR && user.getTotpSecret() != null) {
			return AuthResponse.builder()
					.email(user.getEmail())
					.name(user.getName())
					.role(user.getRole().name())
					.mfaRequired(true)
					.build();
		}

		String token = jwtService.generateToken(user.getEmail());

		return AuthResponse.builder()
				.token(token)
				.email(user.getEmail())
				.name(user.getName())
				.role(user.getRole().name())
				.mfaRequired(false)
				.build();
	}

	@PreAuthorize("permitAll()")
	public AuthResponse verifyMfa(MfaVerifyRequest request) {
		User user =
				userRepository
						.findByEmail(request.getEmail())
						.orElseThrow(() -> new UnauthorizedException("Invalid email"));

		if (user.getRole() != Role.ROLE_ADMINISTRATOR) {
			throw new UnauthorizedException("MFA is only required for administrator accounts");
		}

		if (user.getTotpSecret() == null) {
			throw new UnauthorizedException("MFA not configured for this account");
		}

		if (!verifyTotp(user.getTotpSecret(), request.getCode())) {
			throw new UnauthorizedException("Invalid MFA code");
		}

		String token = jwtService.generateToken(user.getEmail());

		return AuthResponse.builder()
				.token(token)
				.email(user.getEmail())
				.name(user.getName())
				.role(user.getRole().name())
				.mfaRequired(false)
				.build();
	}

	public String generateTotpSecret() {
		byte[] secret = new byte[20];
		new SecureRandom().nextBytes(secret);
		return Base64.getEncoder().encodeToString(secret);
	}

	@PreAuthorize("isAuthenticated()")
	public UserResponse getCurrentUser(String email) {
		User user =
				userRepository
						.findByEmail(email)
						.orElseThrow(() -> new IllegalArgumentException("User not found"));

		return UserResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.role(user.getRole().name())
				.createdAt(user.getCreatedAt())
				.build();
	}

	private boolean isAccountSuspended(User user) {
		return user.getAccountStatus() == AccountStatus.SUSPENDED;
	}

	private boolean isAccountLocked(User user) {
		if (user.getAccountStatus() == AccountStatus.LOCKED) {
			if (user.getLockTime() != null
					&& user.getLockTime()
							.plusMinutes(LOCK_DURATION_MINUTES)
							.isBefore(LocalDateTime.now())) {
				resetLockout(user);
				return false;
			}
			return true;
		}
		return false;
	}

	private void handleFailedLogin(User user) {
		LocalDateTime now = LocalDateTime.now();
		if (user.getLastFailedAttemptTime() != null
				&& user.getLastFailedAttemptTime().plusMinutes(LOCK_WINDOW_MINUTES).isBefore(now)) {
			user.setFailedAttempts(0);
		}
		user.setFailedAttempts(user.getFailedAttempts() + 1);
		user.setLastFailedAttemptTime(now);
		if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
			user.setAccountStatus(AccountStatus.LOCKED);
			user.setLockTime(now);
		}
		userRepository.save(user);
	}

	private void resetLockout(User user) {
		user.setFailedAttempts(0);
		user.setAccountStatus(AccountStatus.ACTIVE);
		user.setLockTime(null);
		user.setLastFailedAttemptTime(null);
		userRepository.save(user);
	}

	private boolean verifyTotp(String encodedSecret, String code) {
		try {
			byte[] secret = Base64.getDecoder().decode(encodedSecret);
			long timeWindow = Instant.now().getEpochSecond() / TOTP_PERIOD;

			for (int i = -1; i <= 1; i++) {
				String expected = generateTotpCode(secret, timeWindow + i);
				if (expected.equals(code)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private String generateTotpCode(byte[] secret, long counter) {
		try {
			byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();
			Mac mac = Mac.getInstance(TOTP_ALGORITHM);
			mac.init(new SecretKeySpec(secret, TOTP_ALGORITHM));
			byte[] hash = mac.doFinal(counterBytes);
			int offset = hash[hash.length - 1] & 0x0F;
			int binary =
					((hash[offset] & 0x7F) << 24)
							| ((hash[offset + 1] & 0xFF) << 16)
							| ((hash[offset + 2] & 0xFF) << 8)
							| (hash[offset + 3] & 0xFF);
			int otp = binary % (int) Math.pow(10, TOTP_DIGITS);
			return String.format("%0" + TOTP_DIGITS + "d", otp);
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate TOTP code", e);
		}
	}
}
