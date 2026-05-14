package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.AuthResponse;
import pt.isep.desofs.vendnet.api.dto.LoginRequest;
import pt.isep.desofs.vendnet.api.dto.MfaVerifyRequest;
import pt.isep.desofs.vendnet.api.dto.RegisterRequest;
import pt.isep.desofs.vendnet.application.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@PreAuthorize("permitAll()")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	@PreAuthorize("permitAll()")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/mfa/verify")
	@PreAuthorize("permitAll()")
	public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
		AuthResponse response = authService.verifyMfa(request);
		return ResponseEntity.ok(response);
	}
}
