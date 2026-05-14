package pt.isep.desofs.vendnet.api.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, String>> dashboard() {
		return ResponseEntity.ok(
				Map.of(
						"message", "Welcome to the admin dashboard",
						"auth", "hasRole('ADMINISTRATOR') — single role"));
	}

	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, String>> listUsers() {
		return ResponseEntity.ok(
				Map.of(
						"message", "Admin-only user list endpoint",
						"auth", "hasRole('ADMINISTRATOR')"));
	}

	@GetMapping("/reports")
	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public ResponseEntity<Map<String, String>> reports() {
		return ResponseEntity.ok(
				Map.of(
						"message", "Reports accessible by admin only",
						"auth", "hasRole('ADMINISTRATOR')"));
	}
}
