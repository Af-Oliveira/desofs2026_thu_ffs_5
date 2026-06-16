package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuditLogRepository auditLogRepository;

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public Map<String, Object> getDashboard() {
		List<User> users = userRepository.findAll();
		long activeCount = users.stream()
				.filter(u -> u.getAccountStatus() == AccountStatus.ACTIVE)
				.count();
		long lockedCount = users.stream()
				.filter(u -> u.getAccountStatus() == AccountStatus.LOCKED)
				.count();
		long suspendedCount = users.stream()
				.filter(u -> u.getAccountStatus() == AccountStatus.SUSPENDED)
				.count();

		return Map.of(
				"message", "Welcome to the admin dashboard",
				"totalUsers", (long) users.size(),
				"activeUsers", activeCount,
				"lockedUsers", lockedCount,
				"suspendedUsers", suspendedCount);
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public List<UserResponse> listUsers() {
		return userRepository.findAll().stream()
				.map(u -> UserResponse.builder()
						.id(u.getId())
						.email(u.getEmail())
						.name(u.getName())
						.role(u.getRole().name())
						.createdAt(u.getCreatedAt())
						.build())
				.collect(Collectors.toList());
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public UserResponse createUser(String email, String password, String name, String roleStr) {
		String username = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
		return createUser(username, email, password, name, roleStr, 0L);
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public UserResponse createUser(
			String username, String email, String password, String fullName, String roleStr, Long adminId) {
		if (userRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("Username already taken");
		}
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already registered");
		}

		Role role;
		try {
			role = Role.valueOf(roleStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Invalid role: " + roleStr
							+ ". Must be ROLE_CUSTOMER, ROLE_OPERATOR, or ROLE_ADMINISTRATOR");
		}

		LocalDateTime now = LocalDateTime.now();
		User user = User.builder()
				.username(username)
				.email(email)
				.password(passwordEncoder.encode(password))
				.name(fullName)
				.role(role)
				.accountStatus(AccountStatus.ACTIVE)
				.createdAt(now)
				.updatedAt(now)
				.build();

		User saved = userRepository.save(user);

		auditLogRepository.save(
				AuditLog.builder()
						.eventType("USER_CREATED")
						.principal(String.valueOf(adminId))
						.details("Created user " + saved.getUsername() + " with role " + saved.getRole())
						.resource("User")
						.action("CREATE")
						.outcome("SUCCESS")
						.timestamp(now)
						.build());

		return UserResponse.builder()
				.id(saved.getId())
				.email(saved.getEmail())
				.name(saved.getName())
				.role(saved.getRole().name())
				.createdAt(saved.getCreatedAt())
				.build();
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public UserResponse updateUser(
			Long userId, String name, String roleStr, String accountStatusStr) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

		if (name != null) {
			user.setName(name);
		}

		if (roleStr != null) {
			try {
				user.setRole(Role.valueOf(roleStr));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid role: " + roleStr);
			}
		}

		if (accountStatusStr != null) {
			try {
				user.setAccountStatus(AccountStatus.valueOf(accountStatusStr));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Invalid account status: " + accountStatusStr);
			}
		}

		user.setUpdatedAt(LocalDateTime.now());
		User saved = userRepository.save(user);

		return UserResponse.builder()
				.id(saved.getId())
				.email(saved.getEmail())
				.name(saved.getName())
				.role(saved.getRole().name())
				.createdAt(saved.getCreatedAt())
				.build();
	}
}
