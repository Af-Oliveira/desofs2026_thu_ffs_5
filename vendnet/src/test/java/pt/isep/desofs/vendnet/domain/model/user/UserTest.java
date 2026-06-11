package pt.isep.desofs.vendnet.domain.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pt.isep.desofs.vendnet.domain.exception.AccountLockedException;
import pt.isep.desofs.vendnet.domain.exception.DisabledException;

class UserTest {

	@Test
	void builder_shouldCreateUserWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
				.id(1L)
				.email("test@vendnet.com")
				.password("encodedPassword")
				.name("Test User")
				.role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.ACTIVE)
				.createdAt(now)
				.updatedAt(now)
				.failedAttempts(0)
				.lockTime(null)
				.lastFailedAttemptTime(null)
				.totpSecret(null)
				.build();

		assertEquals(1L, user.getId());
		assertEquals("test@vendnet.com", user.getEmail());
		assertEquals("encodedPassword", user.getPassword());
		assertEquals("Test User", user.getName());
		assertEquals(Role.ROLE_CUSTOMER, user.getRole());
		assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
		assertEquals(now, user.getCreatedAt());
		assertEquals(now, user.getUpdatedAt());
		assertEquals(0, user.getFailedAttempts());
		assertNull(user.getLockTime());
		assertNull(user.getLastFailedAttemptTime());
		assertNull(user.getTotpSecret());
	}

	@Test
	void builder_defaults_shouldBeCorrect() {
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
				.email("test@vendnet.com")
				.password("encodedPassword")
				.name("Test User")
				.role(Role.ROLE_CUSTOMER)
				.createdAt(now)
				.updatedAt(now)
				.build();

		assertNull(user.getId());
		assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
		assertEquals(0, user.getFailedAttempts());
		assertNull(user.getLockTime());
		assertNull(user.getLastFailedAttemptTime());
		assertNull(user.getTotpSecret());
	}

	@Test
	void setters_shouldModifyFields() {
		User user = new User();
		LocalDateTime now = LocalDateTime.now();

		user.setId(2L);
		user.setEmail("admin@vendnet.com");
		user.setPassword("adminPass");
		user.setName("Admin User");
		user.setRole(Role.ROLE_ADMINISTRATOR);
		user.setAccountStatus(AccountStatus.LOCKED);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.setFailedAttempts(5);
		user.setLockTime(now);
		user.setLastFailedAttemptTime(now);
		user.setTotpSecret("secret12345678901234567890");

		assertEquals(2L, user.getId());
		assertEquals("admin@vendnet.com", user.getEmail());
		assertEquals("adminPass", user.getPassword());
		assertEquals("Admin User", user.getName());
		assertEquals(Role.ROLE_ADMINISTRATOR, user.getRole());
		assertEquals(AccountStatus.LOCKED, user.getAccountStatus());
		assertEquals(now, user.getCreatedAt());
		assertEquals(now, user.getUpdatedAt());
		assertEquals(5, user.getFailedAttempts());
		assertNotNull(user.getLockTime());
		assertNotNull(user.getLastFailedAttemptTime());
		assertEquals("secret12345678901234567890", user.getTotpSecret());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyUser() {
		User user = new User();

		assertNull(user.getId());
		assertNull(user.getEmail());
		assertNull(user.getPassword());
		assertNull(user.getName());
		assertNull(user.getRole());
		assertNotNull(user.getAccountStatus());
		assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
		assertEquals(0, user.getFailedAttempts());
	}

	@Test
	void allArgsConstructor_shouldCreateUserWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		User user = new User(
				3L, "ops_vendnet", "ops@vendnet.com", "pass", "Operator", Role.ROLE_OPERATOR,
				AccountStatus.LOCKED, now, now, 3, now, now, "totp123");

		assertEquals(3L, user.getId());
		assertEquals("ops@vendnet.com", user.getEmail());
		assertEquals("pass", user.getPassword());
		assertEquals("Operator", user.getName());
		assertEquals(Role.ROLE_OPERATOR, user.getRole());
		assertEquals(AccountStatus.LOCKED, user.getAccountStatus());
		assertEquals(now, user.getCreatedAt());
		assertEquals(now, user.getUpdatedAt());
		assertEquals(3, user.getFailedAttempts());
		assertEquals(now, user.getLockTime());
		assertEquals(now, user.getLastFailedAttemptTime());
		assertEquals("totp123", user.getTotpSecret());
	}

	@Test
	void checkAccountStatus_suspended_shouldThrowDisabledException() {
		User user = activeUser();
		user.setAccountStatus(AccountStatus.SUSPENDED);
		assertThrows(DisabledException.class, user::checkAccountStatus);
	}

	@Test
	void checkAccountStatus_lockExpired_shouldResetAndAllowAccess() {
		User user = activeUser();
		user.setAccountStatus(AccountStatus.LOCKED);
		user.setLockTime(LocalDateTime.now().minusMinutes(31));

		user.checkAccountStatus();

		assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
		assertEquals(0, user.getFailedAttempts());
	}

	@Test
	void incrementFailedAttempts_outsideWindow_shouldResetCounterBeforeIncrement() {
		User user = activeUser();
		user.setFailedAttempts(4);
		user.setLastFailedAttemptTime(LocalDateTime.now().minusMinutes(20));

		user.incrementFailedAttempts();

		assertEquals(1, user.getFailedAttempts());
	}

	@Test
	void incrementFailedAttempts_maxAttempts_shouldLockAccount() {
		User user = activeUser();
		user.setFailedAttempts(4);

		user.incrementFailedAttempts();

		assertEquals(AccountStatus.LOCKED, user.getAccountStatus());
		assertNotNull(user.getLockTime());
	}

	@Test
	void checkAccountStatus_lockedWithoutLockTime_shouldThrowAccountLockedException() {
		User user = activeUser();
		user.setAccountStatus(AccountStatus.LOCKED);
		user.setLockTime(null);

		assertThrows(AccountLockedException.class, user::checkAccountStatus);
	}

	@Test
	void checkAccountStatus_locked_shouldThrowAccountLockedException() {
		User user = activeUser();
		user.setAccountStatus(AccountStatus.LOCKED);
		user.setLockTime(LocalDateTime.now());

		assertThrows(AccountLockedException.class, user::checkAccountStatus);
	}

	private User activeUser() {
		LocalDateTime now = LocalDateTime.now();
		return User.builder()
				.email("test@vendnet.com")
				.password("encodedPassword")
				.name("Test User")
				.role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.ACTIVE)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}

	@Test
	void failedAttempts_shouldBeIncremented() {
		LocalDateTime now = LocalDateTime.now();
		User user = User.builder()
				.email("test@vendnet.com")
				.password("encodedPassword")
				.name("Test User")
				.role(Role.ROLE_CUSTOMER)
				.createdAt(now)
				.updatedAt(now)
				.build();

		user.setFailedAttempts(user.getFailedAttempts() + 1);
		assertEquals(1, user.getFailedAttempts());

		user.setFailedAttempts(user.getFailedAttempts() + 1);
		assertEquals(2, user.getFailedAttempts());
	}
}
