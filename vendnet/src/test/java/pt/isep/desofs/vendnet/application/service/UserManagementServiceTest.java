package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuditLogRepository auditLogRepository;

	private UserManagementService userManagementService;

	@BeforeEach
	void setUp() {
		userManagementService = new UserManagementService(userRepository, passwordEncoder, auditLogRepository);
	}

	@Test
	void getDashboard_shouldReturnStats() {
		LocalDateTime now = LocalDateTime.now();
		User active1 = User.builder().id(1L).email("a@test.com").name("A")
				.role(Role.ROLE_CUSTOMER).accountStatus(AccountStatus.ACTIVE)
				.createdAt(now).updatedAt(now).build();
		User active2 = User.builder().id(2L).email("b@test.com").name("B")
				.role(Role.ROLE_OPERATOR).accountStatus(AccountStatus.ACTIVE)
				.createdAt(now).updatedAt(now).build();
		User locked = User.builder().id(3L).email("c@test.com").name("C")
				.role(Role.ROLE_CUSTOMER).accountStatus(AccountStatus.LOCKED)
				.createdAt(now).updatedAt(now).build();
		when(userRepository.findAll()).thenReturn(List.of(active1, active2, locked));

		Map<String, Object> result = userManagementService.getDashboard();

		assertEquals("Welcome to the admin dashboard", result.get("message"));
		assertEquals(3L, result.get("totalUsers"));
		assertEquals(2L, result.get("activeUsers"));
		assertEquals(1L, result.get("lockedUsers"));
		assertEquals(0L, result.get("suspendedUsers"));
	}

	@Test
	void listUsers_shouldReturnAllUsers() {
		LocalDateTime now = LocalDateTime.now();
		User user = User.builder().id(1L).email("test@vendnet.io").name("Test")
				.role(Role.ROLE_CUSTOMER).accountStatus(AccountStatus.ACTIVE)
				.createdAt(now).updatedAt(now).build();
		when(userRepository.findAll()).thenReturn(List.of(user));

		List<UserResponse> result = userManagementService.listUsers();

		assertEquals(1, result.size());
		assertEquals("test@vendnet.io", result.get(0).getEmail());
		assertEquals("Test", result.get(0).getName());
		assertEquals("ROLE_CUSTOMER", result.get(0).getRole());
	}

	@Test
	void createUser_shouldCreateAndReturnUser() {
		when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
		when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
		LocalDateTime now = LocalDateTime.now();
		User saved = User.builder().id(1L).email("new@test.com").name("New")
				.password("encoded_pass").role(Role.ROLE_CUSTOMER)
				.accountStatus(AccountStatus.ACTIVE).createdAt(now).updatedAt(now).build();
		when(userRepository.save(any(User.class))).thenReturn(saved);

		UserResponse result = userManagementService.createUser(
				"new@test.com", "pass123", "New", "ROLE_CUSTOMER");

		assertNotNull(result);
		assertEquals("new@test.com", result.getEmail());
		assertEquals("New", result.getName());
		assertEquals("ROLE_CUSTOMER", result.getRole());
	}

	@Test
	void createUser_duplicateEmail_shouldThrowException() {
		when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

		assertThrows(IllegalArgumentException.class,
				() -> userManagementService.createUser(
						"existing@test.com", "pass123", "Name", "ROLE_CUSTOMER"));
	}

	@Test
	void createUser_invalidRole_shouldThrowException() {
		when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

		assertThrows(IllegalArgumentException.class,
				() -> userManagementService.createUser(
						"test@test.com", "pass123", "Name", "INVALID_ROLE"));
	}

	@Test
	void updateUser_shouldUpdateRole() {
		LocalDateTime now = LocalDateTime.now();
		User user = User.builder().id(1L).email("test@test.com").name("Test")
				.role(Role.ROLE_CUSTOMER).accountStatus(AccountStatus.ACTIVE)
				.createdAt(now).updatedAt(now).build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserResponse result = userManagementService.updateUser(
				1L, null, "ROLE_OPERATOR", null);

		assertNotNull(result);
		assertEquals("ROLE_OPERATOR", result.getRole());
	}

	@Test
	void updateUser_shouldSuspendAccount() {
		LocalDateTime now = LocalDateTime.now();
		User user = User.builder().id(1L).email("test@test.com").name("Test")
				.role(Role.ROLE_CUSTOMER).accountStatus(AccountStatus.ACTIVE)
				.createdAt(now).updatedAt(now).build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserResponse result = userManagementService.updateUser(
				1L, null, null, "SUSPENDED");

		assertNotNull(result);
	}

	@Test
	void updateUser_notFound_shouldThrowException() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> userManagementService.updateUser(999L, null, null, null));
	}

	@Test
	void createUser_usernameAlreadyTaken_shouldThrowException() {
		when(userRepository.existsByUsername("taken")).thenReturn(true);

		assertThrows(
				IllegalArgumentException.class,
				() ->
						userManagementService.createUser(
								"taken", "new@test.com", "pass123", "New User", "ROLE_CUSTOMER", 1L));
	}

	@Test
	void updateUser_invalidRole_shouldThrowException() {
		LocalDateTime now = LocalDateTime.now();
		User user =
				User.builder()
						.id(1L)
						.email("test@test.com")
						.name("Test")
						.role(Role.ROLE_CUSTOMER)
						.accountStatus(AccountStatus.ACTIVE)
						.createdAt(now)
						.updatedAt(now)
						.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		assertThrows(
				IllegalArgumentException.class,
				() -> userManagementService.updateUser(1L, null, "BAD_ROLE", null));
	}

	@Test
	void updateUser_invalidAccountStatus_shouldThrowException() {
		LocalDateTime now = LocalDateTime.now();
		User user =
				User.builder()
						.id(1L)
						.email("test@test.com")
						.name("Test")
						.role(Role.ROLE_CUSTOMER)
						.accountStatus(AccountStatus.ACTIVE)
						.createdAt(now)
						.updatedAt(now)
						.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		assertThrows(
				IllegalArgumentException.class,
				() -> userManagementService.updateUser(1L, null, null, "INVALID_STATUS"));
	}

	@Test
	void updateUser_shouldUpdateName() {
		LocalDateTime now = LocalDateTime.now();
		User user =
				User.builder()
						.id(1L)
						.email("test@test.com")
						.name("Old")
						.role(Role.ROLE_CUSTOMER)
						.accountStatus(AccountStatus.ACTIVE)
						.createdAt(now)
						.updatedAt(now)
						.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

		UserResponse result = userManagementService.updateUser(1L, "New Name", null, null);

		assertEquals("New Name", result.getName());
	}
}
