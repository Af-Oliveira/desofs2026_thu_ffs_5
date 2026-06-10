package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.desofs.vendnet.api.dto.TelemetryRequest;
import pt.isep.desofs.vendnet.api.dto.TelemetryResponse;
import pt.isep.desofs.vendnet.domain.exception.ForbiddenOperationException;
import pt.isep.desofs.vendnet.domain.exception.RateLimitException;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.model.machine.MachineStatus;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryService {

	private final TelemetryRepository telemetryRepository;
	private final VendingMachineRepository machineRepository;
	private final AuditLogRepository auditLogRepository;

	private static final int MAX_REQUESTS_PER_MINUTE = 2;

	@PreAuthorize("permitAll()")
	@Transactional
	public TelemetryResponse ingest(TelemetryRequest request, String certificateCn) {
		if (certificateCn == null || certificateCn.isBlank()) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("CERTIFICATE_MISSING")
							.details("Telemetry submitted without client certificate")
							.resource("Telemetry")
							.action("INGEST")
							.outcome("REJECTED")
							.timestamp(LocalDateTime.now())
							.build());
			throw new pt.isep.desofs.vendnet.domain.exception.UnauthorizedException(
					"Machine client certificate required");
		}

		VendingMachine machine =
				machineRepository
						.findByCode(certificateCn)
						.orElseThrow(
								() -> {
									auditLogRepository.save(
											AuditLog.builder()
													.eventType("UNKNOWN_MACHINE")
													.principal(certificateCn)
													.details("Machine certificate CN is not registered")
													.resource("Telemetry")
													.action("IDENTITY_CHECK")
													.outcome("REJECTED")
													.timestamp(LocalDateTime.now())
													.build());
									return new ForbiddenOperationException("Machine not registered");
								});

		if (!machine.isActive()) {
			throw new ForbiddenOperationException("Machine not active");
		}

		if (!certificateCn.equals(request.getSerialNumber())) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("IDENTITY_MISMATCH")
							.principal(certificateCn)
							.details(
									"Certificate CN "
											+ certificateCn
											+ " != request serial "
											+ request.getSerialNumber())
							.resource("Telemetry")
							.action("IDENTITY_CHECK")
							.outcome("REJECTED")
							.timestamp(LocalDateTime.now())
							.build());
			throw new ForbiddenOperationException("Identity mismatch");
		}

		long recent =
				telemetryRepository.countByMachineIdAndTimestampAfter(
						machine.getId(), LocalDateTime.now().minusMinutes(1));
		if (recent >= MAX_REQUESTS_PER_MINUTE) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("MACHINE_RATE_LIMIT_EXCEEDED")
							.principal(machine.getCode())
							.details("Telemetry rate limit exceeded")
							.resource("Telemetry")
							.action("RATE_LIMIT")
							.outcome("REJECTED")
							.timestamp(LocalDateTime.now())
							.build());
			throw new RateLimitException("Telemetry rate limit exceeded");
		}

		MachineTelemetry telemetry =
				MachineTelemetry.builder()
						.machine(machine)
						.temperatureCelsius(request.getTemperature())
						.status(request.getStatusCode())
						.timestamp(request.getTimestamp())
						.build();

		MachineTelemetry saved = telemetryRepository.save(telemetry);

		machine.setLastTelemetryAt(request.getTimestamp());
		try {
			machine.setStatus(MachineStatus.valueOf(request.getStatusCode()));
		} catch (IllegalArgumentException ignored) {
			machine.setStatus(MachineStatus.ONLINE);
		}
		machine.setUpdatedAt(LocalDateTime.now());
		machineRepository.save(machine);

		List<String> alerts = evaluateAlerts(request);
		for (String alert : alerts) {
			auditLogRepository.save(
					AuditLog.builder()
							.eventType("TELEMETRY_ALERT")
							.principal(machine.getCode())
							.details(alert)
							.resource("Telemetry")
							.action("ALERT")
							.outcome("TRIGGERED")
							.timestamp(LocalDateTime.now())
							.build());
		}

		auditLogRepository.save(
				AuditLog.builder()
						.eventType("TELEMETRY_INGESTED")
						.principal(machine.getCode())
						.details("Telemetry received from machine, id=" + saved.getId())
						.resource("Telemetry")
						.action("INGEST")
						.outcome("SUCCESS")
						.timestamp(LocalDateTime.now())
						.build());

		return TelemetryResponse.builder().accepted(true).alertsRaised(alerts.size()).build();
	}

	@PreAuthorize("permitAll()")
	@Transactional
	public MachineTelemetry save(MachineTelemetry telemetry, String certificateCn) {
		if (telemetry.getMachine() != null && telemetry.getMachine().getId() == null) {
			VendingMachine machine =
					machineRepository
							.findByCode(telemetry.getMachine().getCode())
							.orElseThrow(
									() ->
											new IllegalArgumentException(
													"Machine not found: "
															+ telemetry.getMachine().getCode()));

			if (certificateCn != null && !certificateCn.equals(machine.getCode())) {
				log.warn(
						"Certificate CN mismatch: cert={}, machine={}",
						certificateCn,
						machine.getCode());
				auditLogRepository.save(
						AuditLog.builder()
								.eventType("IDENTITY_MISMATCH")
								.details(
										"Certificate CN "
												+ certificateCn
												+ " != machine code "
												+ machine.getCode())
								.resource("Telemetry")
								.action("IDENTITY_CHECK")
								.outcome("REJECTED")
								.timestamp(LocalDateTime.now())
								.build());
				throw new IllegalArgumentException(
						"Certificate identity mismatch with machine");
			}

			machine.setLastTelemetryAt(LocalDateTime.now());
			if (telemetry.getStatus() != null) {
				try {
					machine.setStatus(MachineStatus.valueOf(telemetry.getStatus()));
				} catch (IllegalArgumentException ignored) {
					machine.setStatus(MachineStatus.ONLINE);
				}
			} else {
				machine.setStatus(MachineStatus.ONLINE);
			}
			machine.setUpdatedAt(LocalDateTime.now());
			machineRepository.save(machine);

			telemetry.setMachine(machine);
		}

		MachineTelemetry saved = telemetryRepository.save(telemetry);

		auditLogRepository.save(
				AuditLog.builder()
						.eventType("TELEMETRY_INGESTED")
						.principal(telemetry.getMachine() != null ? telemetry.getMachine().getCode() : "unknown")
						.details("Telemetry received from machine")
						.resource("Telemetry")
						.action("INGEST")
						.outcome("SUCCESS")
						.timestamp(LocalDateTime.now())
						.build());

		return saved;
	}

	private List<String> evaluateAlerts(TelemetryRequest request) {
		List<String> alerts = new ArrayList<>();
		if (request.getTemperature() != null
				&& request.getTemperature().compareTo(new java.math.BigDecimal("45.0")) > 0) {
			alerts.add("HIGH_TEMPERATURE");
		}
		if (request.getErrorCodes() != null && !request.getErrorCodes().isEmpty()) {
			alerts.add("ERROR_CODES:" + String.join(",", request.getErrorCodes()));
		}
		if (request.getStockLevels() != null
				&& request.getStockLevels().values().stream()
						.anyMatch(stock -> stock != null && stock < 2)) {
			alerts.add("LOW_STOCK");
		}
		return alerts;
	}

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<MachineTelemetry> findByMachineId(Long machineId) {
		return telemetryRepository.findByMachineId(machineId);
	}
}
