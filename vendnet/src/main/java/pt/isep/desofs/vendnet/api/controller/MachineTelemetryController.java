package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.application.service.TelemetryService;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class MachineTelemetryController {

	private final TelemetryService telemetryService;

	@PostMapping
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<String, String>> ingest(
			@Valid @RequestBody MachineTelemetry telemetry) {
		telemetryService.save(telemetry);
		return ResponseEntity.ok(Map.of("status", "telemetry ingested"));
	}
}
