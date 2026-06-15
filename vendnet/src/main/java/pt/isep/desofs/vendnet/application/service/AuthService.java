package pt.isep.desofs.vendnet.application.service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
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
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final int TOTP_PERIOD = 30;
	private static final int TOTP_DIGITS = 6;
	private static final String TOTP_ALGORITHM = "HmacSHA1";
	private static final int MAX_USERNAME_LENGTH = 30;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuditLogRepository auditLogRepository;

	@PreAuthorize("permitAll()")
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		LocalDateTime now = LocalDateTime.now();
		String username = uniqueRegistrationUsername(request.getEmail(), now);

		User user =
				User.builder()
						.username(username)
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.name(request.getName())
						.role(Role.ROLE_CUSTOMER)
						.accountStatus(AccountStatus.ACTIVE)
						.createdAt(now)
						.updatedAt(now)
						.build();

		userRepository.save(user);

		String token = issueAccessToken(user);

		auditLogRepository.save(
				AuditLog.builder()
						.eventType("REGISTER")
						.principal(user.getEmail())
						.details("User registered: " + user.getEmail())
						.resource("User")
						.action("REGISTER")
						.outcome("SUCCESS")
						.timestamp(now)
						.build());

		return AuthResponse.builder()
				.token(token)
				.accessToken(token)
				.refreshToken(jwtService.generateRefreshToken(user.getId()))
				.expiresIn(jwtService.getExpirationSeconds())
				.email(user.getEmail())
				.username(user.getUsername())
				.name(user.getName())
				.role(user.getRole().name())
				.mfaRequired(false)
				.build();
	}

	@PreAuthorize("permitAll()")
	public AuthResponse login(LoginRequest request) {
		Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
		if (userOpt.isEmpty()
				&& request.getUsername() != null
				&& request.getUsername().contains("@")) {
			userOpt = userRepository.findByEmail(request.getUsername());
		}

		if (userOpt.isEmpty()) {
			passwordEncoder.matches(request.getPassword(), passwordEncoder.encode(dummyPassword()));
			throw new UnauthorizedException("Invalid username or password");
		}

		User user = userOpt.get();

		try {
			user.checkAccountStatus();
		} catch (DisabledException e) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("LOGIN_DENIED_INACTIVE")
							.principal(user.getUsername())
							.details("Account is suspended")
							.resource("User")
							.action("LOGIN")
							.outcome("DENIED")
							.timestamp(LocalDateTime.now())
							.build());
			throw e;
		} catch (AccountLockedException e) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("LOGIN_DENIED_INACTIVE")
							.principal(user.getUsername())
							.details("Account is locked")
							.resource("User")
							.action("LOGIN")
							.outcome("DENIED")
							.timestamp(LocalDateTime.now())
							.build());
			throw e;
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			user.incrementFailedAttempts();
			userRepository.save(user);

			if (user.getAccountStatus() == AccountStatus.LOCKED) {
				auditLogRepository.save(
						AuditLog.builder()
								.eventType("ACCOUNT_LOCKED")
								.principal(user.getUsername())
								.details(
										"Account locked after "
												+ user.getFailedAttempts()
												+ " failed attempts")
								.resource("User")
								.action("LOCK")
								.outcome("LOCKED")
								.timestamp(LocalDateTime.now())
								.build());
				throw new AccountLockedException(
						"Account is temporarily locked. Try again in 30 minutes.");
			}

			auditLogRepository.save(
					AuditLog.builder()
							.eventType("LOGIN_FAILED")
							.principal(user.getUsername())
							.details("Invalid password")
							.resource("User")
							.action("LOGIN")
							.outcome("FAILED")
							.timestamp(LocalDateTime.now())
							.build());
			throw new UnauthorizedException("Invalid username or password");
		}

		user.resetFailedAttempts();
		userRepository.save(user);

		auditLogRepository.save(
				AuditLog.builder()
						.eventType("LOGIN_SUCCESS")
						.principal(user.getUsername())
						.details("User logged in successfully")
						.resource("User")
						.action("LOGIN")
						.outcome("SUCCESS")
						.timestamp(LocalDateTime.now())
						.build());

		if (user.getRole() == Role.ROLE_ADMINISTRATOR && user.getTotpSecret() != null) {
			return AuthResponse.builder()
					.email(user.getEmail())
					.username(user.getUsername())
					.name(user.getName())
					.role(user.getRole().name())
					.mfaRequired(true)
					.build();
		}

		String token = issueAccessToken(user);

		return AuthResponse.builder()
				.token(token)
				.accessToken(token)
				.refreshToken(jwtService.generateRefreshToken(user.getId()))
				.expiresIn(jwtService.getExpirationSeconds())
				.email(user.getEmail())
				.username(user.getUsername())
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

		String token = issueAccessToken(user);

		return AuthResponse.builder()
				.token(token)
				.accessToken(token)
				.refreshToken(jwtService.generateRefreshToken(user.getId()))
				.expiresIn(jwtService.getExpirationSeconds())
				.email(user.getEmail())
				.username(user.getUsername())
				.name(user.getName())
				.role(user.getRole().name())
				.mfaRequired(false)
				.build();
	}

	public String generateTotpSecret() {
		byte[] secret = new byte[20];
		SECURE_RANDOM.nextBytes(secret);
		return Base64.getEncoder().encodeToString(secret);
	}

	private String dummyPassword() {
		byte[] secret = new byte[32];
		SECURE_RANDOM.nextBytes(secret);
		return Base64.getEncoder().encodeToString(secret);
	}

	private String issueAccessToken(User user) {
		if (user.getId() != null) {
			return jwtService.generateToken(user.getId(), user.getRole().name());
		}
		return jwtService.generateToken(user.getEmail());
	}

	private String uniqueRegistrationUsername(String email, LocalDateTime now) {
		int atIndex = email.indexOf('@');
		String base =
				(atIndex > 0 ? email.substring(0, atIndex) : email).replaceAll("[^A-Za-z0-9]", "");
		if (base.length() < 3) {
			base = "user" + now.getNano();
		}
		base = limitUsername(base);

		String candidate = base;
		int attempt = 0;
		while (userRepository.existsByUsername(candidate)) {
			String suffix = String.valueOf(now.getNano() + attempt++);
			int prefixLength = Math.max(3, MAX_USERNAME_LENGTH - suffix.length());
			candidate =
					limitUsername(base).substring(0, Math.min(base.length(), prefixLength))
							+ suffix;
			if (attempt > 1000) {
				throw new IllegalStateException("Unable to generate unique username");
			}
		}
		return candidate;
	}

	private String limitUsername(String username) {
		if (username.length() <= MAX_USERNAME_LENGTH) {
			return username;
		}
		return username.substring(0, MAX_USERNAME_LENGTH);
	}

	@PreAuthorize("isAuthenticated()")
	public UserResponse getCurrentUser(String username) {
		User user =
				userRepository
						.findByUsername(username)
						.or(() -> userRepository.findByEmail(username))
						.orElseThrow(() -> new IllegalArgumentException("User not found"));

		return UserResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.role(user.getRole().name())
				.createdAt(user.getCreatedAt())
				.build();
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
