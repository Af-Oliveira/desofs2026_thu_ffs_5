package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	private AuthService authService;

	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, jwtService);
		now = LocalDateTime.now();
	}

	// ── Register ────────────────────────────────────────────────────────────

	@Test
	void register_shouldCreateUserAndReturnToken() {
		RegisterRequest request = new RegisterRequest("new@vendnet.com", "password123", "New User");
		when(userRepository.existsByEmail("new@vendnet.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
		when(jwtService.generateToken("new@vendnet.com")).thenReturn("jwt-token");

		AuthResponse response = authService.register(request);

		assertEquals("jwt-token", response.getToken());
		assertEquals("new@vendnet.com", response.getEmail());
		assertEquals("New User", response.getName());
		assertEquals("ROLE_CUSTOMER", response.getRole());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void register_shouldThrowWhenEmailExists() {
		RegisterRequest request = new RegisterRequest("existing@vendnet.com", "password123", "Existing");
		when(userRepository.existsByEmail("existing@vendnet.com")).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> authService.register(request));
	}

	// ── Login ───────────────────────────────────────────────────────────────

	@Test
	void login_shouldReturnTokenWithValidCredentials() {
		LoginRequest request = new LoginRequest("user@vendnet.com", "correctPass");
		User user = buildUser("user@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("user@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("correctPass", "hashedPass")).thenReturn(true);
		when(jwtService.generateToken("user@vendnet.com")).thenReturn("jwt-token");

		AuthResponse response = authService.login(request);

		assertEquals("jwt-token", response.getToken());
		assertEquals("user@vendnet.com", response.getEmail());
		assertEquals("ROLE_CUSTOMER", response.getRole());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void login_shouldThrowWhenUserNotFound() {
		LoginRequest request = new LoginRequest("nobody@vendnet.com", "password");
		when(userRepository.findByEmail("nobody@vendnet.com")).thenReturn(Optional.empty());

		assertThrows(UnauthorizedException.class, () -> authService.login(request));
	}

	@Test
	void login_shouldThrowWhenAccountSuspended() {
		LoginRequest request = new LoginRequest("suspended@vendnet.com", "password");
		User user = buildUser("suspended@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.SUSPENDED);

		when(userRepository.findByEmail("suspended@vendnet.com")).thenReturn(Optional.of(user));

		assertThrows(DisabledException.class, () -> authService.login(request));
	}

	@Test
	void login_shouldThrowWhenAccountLocked() {
		LoginRequest request = new LoginRequest("locked@vendnet.com", "password");
		User user = buildUser("locked@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.LOCKED);
		user.setLockTime(LocalDateTime.now());

		when(userRepository.findByEmail("locked@vendnet.com")).thenReturn(Optional.of(user));

		assertThrows(AccountLockedException.class, () -> authService.login(request));
	}

	@Test
	void login_shouldUnlockWhenLockTimeExpired() {
		LoginRequest request = new LoginRequest("expiredLock@vendnet.com", "correctPass");
		User user = buildUser("expiredLock@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.LOCKED);
		user.setLockTime(LocalDateTime.now().minusMinutes(31));

		when(userRepository.findByEmail("expiredLock@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("correctPass", "hashedPass")).thenReturn(true);
		when(jwtService.generateToken("expiredLock@vendnet.com")).thenReturn("jwt-token");

		AuthResponse response = authService.login(request);

		assertEquals("jwt-token", response.getToken());
		verify(userRepository, times(2)).save(any(User.class));
	}

	@Test
	void login_shouldThrowWhenPasswordIncorrect() {
		LoginRequest request = new LoginRequest("user@vendnet.com", "wrongPass");
		User user = buildUser("user@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("user@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

		assertThrows(UnauthorizedException.class, () -> authService.login(request));
	}

	@Test
	void login_adminShouldRequireMfa_whenTotpSecretSet() {
		LoginRequest request = new LoginRequest("admin@vendnet.com", "correctPass");
		User user = buildUser("admin@vendnet.com", "hashedPass", Role.ROLE_ADMINISTRATOR, AccountStatus.ACTIVE);
		user.setTotpSecret("test-totp-secret-for-mfa");

		when(userRepository.findByEmail("admin@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("correctPass", "hashedPass")).thenReturn(true);

		AuthResponse response = authService.login(request);

		assertTrue(response.isMfaRequired());
		assertEquals("admin@vendnet.com", response.getEmail());
		assertEquals("ROLE_ADMINISTRATOR", response.getRole());
	}

	@Test
	void login_adminWithoutTotpSecret_shouldNotRequireMfa() {
		LoginRequest request = new LoginRequest("admin@vendnet.com", "correctPass");
		User user = buildUser("admin@vendnet.com", "hashedPass", Role.ROLE_ADMINISTRATOR, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("admin@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("correctPass", "hashedPass")).thenReturn(true);
		when(jwtService.generateToken("admin@vendnet.com")).thenReturn("jwt-token");

		AuthResponse response = authService.login(request);

		assertFalse(response.isMfaRequired());
		assertEquals("jwt-token", response.getToken());
	}

	// ── Account lockout ─────────────────────────────────────────────────────

	@Test
	void login_failedAttemptsIncrementAndLock() {
		LoginRequest request = new LoginRequest("user@vendnet.com", "wrongPass");
		User user = buildUser("user@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("user@vendnet.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

		for (int i = 0; i < 5; i++) {
			try {
				authService.login(request);
			} catch (UnauthorizedException ignored) {
			}
		}

		verify(userRepository, times(5)).save(any(User.class));
	}

	// ── MFA Verification ────────────────────────────────────────────────────

	@Test
	void verifyMfa_shouldThrowWhenUserNotFound() {
		MfaVerifyRequest request = new MfaVerifyRequest("nobody@vendnet.com", "123456");
		when(userRepository.findByEmail("nobody@vendnet.com")).thenReturn(Optional.empty());

		assertThrows(UnauthorizedException.class, () -> authService.verifyMfa(request));
	}

	@Test
	void verifyMfa_shouldThrowWhenNotAdmin() {
		MfaVerifyRequest request = new MfaVerifyRequest("customer@vendnet.com", "123456");
		User user = buildUser("customer@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("customer@vendnet.com")).thenReturn(Optional.of(user));

		assertThrows(UnauthorizedException.class, () -> authService.verifyMfa(request));
	}

	@Test
	void verifyMfa_shouldThrowWhenNoTotpSecret() {
		MfaVerifyRequest request = new MfaVerifyRequest("admin@vendnet.com", "123456");
		User user = buildUser("admin@vendnet.com", "hashedPass", Role.ROLE_ADMINISTRATOR, AccountStatus.ACTIVE);

		when(userRepository.findByEmail("admin@vendnet.com")).thenReturn(Optional.of(user));

		assertThrows(UnauthorizedException.class, () -> authService.verifyMfa(request));
	}

	@Test
	void verifyMfa_shouldThrowWhenInvalidCode() {
		MfaVerifyRequest request = new MfaVerifyRequest("admin@vendnet.com", "000000");
		User user = buildUser("admin@vendnet.com", "hashedPass", Role.ROLE_ADMINISTRATOR, AccountStatus.ACTIVE);
		user.setTotpSecret(authService.generateTotpSecret());

		when(userRepository.findByEmail("admin@vendnet.com")).thenReturn(Optional.of(user));

		assertThrows(UnauthorizedException.class, () -> authService.verifyMfa(request));
	}

	// ── Get Current User ────────────────────────────────────────────────────

	@Test
	void getCurrentUser_shouldReturnUserResponse() {
		User user = buildUser("user@vendnet.com", "hashedPass", Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
		user.setId(1L);
		when(userRepository.findByEmail("user@vendnet.com")).thenReturn(Optional.of(user));

		UserResponse response = authService.getCurrentUser("user@vendnet.com");

		assertEquals(1L, response.getId());
		assertEquals("user@vendnet.com", response.getEmail());
		assertEquals("Test User", response.getName());
		assertEquals("ROLE_CUSTOMER", response.getRole());
	}

	@Test
	void getCurrentUser_shouldThrowWhenUserNotFound() {
		when(userRepository.findByEmail("nobody@vendnet.com")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> authService.getCurrentUser("nobody@vendnet.com"));
	}

	// ── Generate TOTP Secret ────────────────────────────────────────────────

	@Test
	void generateTotpSecret_shouldReturnNonEmptyString() {
		String secret = authService.generateTotpSecret();

		assertNotNull(secret);
		assertTrue(secret.length() > 0);
	}

	@Test
	void generateTotpSecret_shouldReturnDifferentValues() {
		String secret1 = authService.generateTotpSecret();
		String secret2 = authService.generateTotpSecret();

		assertNotNull(secret1);
		assertNotNull(secret2);
		assertTrue(!secret1.equals(secret2));
	}

	// ── Helpers ─────────────────────────────────────────────────────────────

	private User buildUser(String email, String password, Role role, AccountStatus status) {
		return User.builder()
				.email(email)
				.password(password)
				.name("Test User")
				.role(role)
				.accountStatus(status)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}
