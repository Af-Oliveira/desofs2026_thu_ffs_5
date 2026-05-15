package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.CreateUserRequest;
import pt.isep.desofs.vendnet.api.dto.UpdateUserRequest;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.application.service.UserManagementService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserManagementService userManagementService;

	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, Object>> dashboard() {
		return ResponseEntity.ok(userManagementService.getDashboard());
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<List<UserResponse>> listUsers() {
		return ResponseEntity.ok(userManagementService.listUsers());
	}

	@PostMapping("/users")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
		UserResponse user = userManagementService.createUser(
				request.getEmail(), request.getPassword(), request.getName(), request.getRole());
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@PutMapping("/users/{userId}")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<UserResponse> updateUser(
			@PathVariable Long userId,
			@Valid @RequestBody UpdateUserRequest request) {
		UserResponse user = userManagementService.updateUser(
				userId,
				request.getName(),
				request.getRole(),
				request.getAccountStatus());
		return ResponseEntity.ok(user);
	}

	@GetMapping("/reports")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, String>> reports() {
		return ResponseEntity.ok(Map.of(
				"message", "Reports accessible by admin only",
				"auth", "hasRole('ADMINISTRATOR')"));
	}
}
