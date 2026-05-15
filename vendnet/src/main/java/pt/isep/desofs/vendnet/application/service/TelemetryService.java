package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<MachineTelemetry> findByMachineId(Long machineId) {
		return telemetryRepository.findByMachineId(machineId);
	}
}
