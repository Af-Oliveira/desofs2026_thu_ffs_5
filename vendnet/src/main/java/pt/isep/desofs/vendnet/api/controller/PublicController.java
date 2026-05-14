package pt.isep.desofs.vendnet.api.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

	@GetMapping("/info")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<String, String>> info() {
		return ResponseEntity.ok(
				Map.of(
						"app", "VendNet",
						"version", "0.0.1",
						"desc", "No authentication required — permitAll() in SecurityConfig"));
	}
}
