package pt.isep.desofs.vendnet.api.controller;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.api.dto.TelemetryRequest;
import pt.isep.desofs.vendnet.api.dto.TelemetryResponse;
import pt.isep.desofs.vendnet.application.service.TelemetryService;

@RestController
@RequiredArgsConstructor
public class MachineTelemetryController {

	private final TelemetryService telemetryService;

	@PostMapping({"/api/machines/telemetry", "/api/telemetry"})
	@PreAuthorize("permitAll()")
	public ResponseEntity<TelemetryResponse> ingest(
			@Valid @RequestBody TelemetryRequest telemetry, HttpServletRequest request) {
		String certificateCn = (String) request.getAttribute("X509_CN");
		TelemetryResponse response = telemetryService.ingest(telemetry, certificateCn);
		return ResponseEntity.ok(response);
	}
}
