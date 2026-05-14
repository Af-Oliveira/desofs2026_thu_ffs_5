package pt.isep.desofs.vendnet.api.controller;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.ClaimsResponse;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.application.service.AuthService;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final AuthService authService;

	@GetMapping("/api/auth/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserResponse> me() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserResponse response = authService.getCurrentUser(auth.getName());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/api/auth/claims")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ClaimsResponse> claims() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String role =
				auth.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority)
						.findFirst()
						.orElse("UNKNOWN");

		ClaimsResponse response =
				ClaimsResponse.builder()
						.subject(auth.getName())
						.role(role)
						.issuedAt(new Date())
						.expiration(null)
						.build();

		return ResponseEntity.ok(response);
	}
}
