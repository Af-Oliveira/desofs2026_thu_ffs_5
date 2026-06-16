package pt.isep.desofs.vendnet.api.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.config.BootstrapReadyIndicator;

@Slf4j
@RestController
public class PingController {

	private final BootstrapReadyIndicator bootstrapReady;

	public PingController(Optional<BootstrapReadyIndicator> bootstrapReady) {
		this.bootstrapReady = bootstrapReady.orElse(null);
	}

	@GetMapping("/api/health/ping")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<String, String>> ping() {
		if (bootstrapReady != null && !bootstrapReady.isReady()) {
			return ResponseEntity.status(503)
					.body(Map.of("status", "seeding", "message", "Bootstrap in progress..."));
		}

		LocalDateTime now = LocalDateTime.now();

		Map<String, String> response =
				Map.of(
						"status",
						"ok",
						"message",
						"Hello World from VendNet!",
						"timestamp",
						now.toString(),
						"uptime",
						java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime()
								+ "ms");

		log.info(
				"PING Hello World | VendNet is alive and ready for calls | timestamp={} | uptime={}ms",
				now,
				response.get("uptime"));

		return ResponseEntity.ok(response);
	}
}
