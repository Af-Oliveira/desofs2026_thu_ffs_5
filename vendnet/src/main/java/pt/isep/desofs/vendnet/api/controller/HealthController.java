package pt.isep.desofs.vendnet.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.config.BootstrapReadyIndicator;

@RestController
@RequestMapping("/api/health")
public class HealthController {

	@Autowired(required = false)
	private BootstrapReadyIndicator bootstrapReady;

	@GetMapping
	@PreAuthorize("permitAll()")
	public ResponseEntity<String> health() {
		if (bootstrapReady != null && !bootstrapReady.isReady()) {
			return ResponseEntity.status(503).body("Seeding...");
		}
		return ResponseEntity.ok("UP");
	}
}
