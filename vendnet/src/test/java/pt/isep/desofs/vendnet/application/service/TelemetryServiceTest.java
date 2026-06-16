package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pt.isep.desofs.vendnet.api.dto.TelemetryRequest;
import pt.isep.desofs.vendnet.domain.exception.ForbiddenOperationException;
import pt.isep.desofs.vendnet.domain.exception.RateLimitException;
import pt.isep.desofs.vendnet.domain.exception.UnauthorizedException;
import pt.isep.desofs.vendnet.domain.model.machine.MachineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TelemetryServiceTest {

	@Mock private TelemetryRepository telemetryRepository;
	@Mock private VendingMachineRepository machineRepository;
	@Mock private AuditLogRepository auditLogRepository;

	private TelemetryService telemetryService;

	@BeforeEach
	void setUp() {
		telemetryService = new TelemetryService(telemetryRepository, machineRepository, auditLogRepository);
	}

	@Test
	void save_withValidMachine_shouldSave() {
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		MachineTelemetry telemetry = MachineTelemetry.builder()
				.machine(machine).cpuUsage(new BigDecimal("45.5")).memoryUsage(new BigDecimal("60.0"))
				.status("ONLINE").timestamp(LocalDateTime.now()).build();
		when(machineRepository.findByCode("VM-001")).thenReturn(Optional.of(machine));
		when(machineRepository.save(any(VendingMachine.class))).thenReturn(machine);
		when(telemetryRepository.save(any(MachineTelemetry.class))).thenAnswer(inv -> {
			MachineTelemetry t = inv.getArgument(0);
			t.setId(1L);
			return t;
		});
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));
		MachineTelemetry result = telemetryService.save(telemetry, "VM-001");
		assertEquals(new BigDecimal("45.5"), result.getCpuUsage());
	}

	@Test
	void save_shouldThrowWhenMachineNotFound() {
		VendingMachine machine = VendingMachine.builder().code("VM-UNKNOWN").build();
		MachineTelemetry telemetry = MachineTelemetry.builder()
				.machine(machine).status("ONLINE").timestamp(LocalDateTime.now()).build();
		when(machineRepository.findByCode("VM-UNKNOWN")).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> telemetryService.save(telemetry, "VM-UNKNOWN"));
	}

	@Test
	void findByMachineId_shouldReturnList() {
		MachineTelemetry t = MachineTelemetry.builder().id(1L).cpuUsage(new BigDecimal("80.0")).status("HIGH_LOAD").build();
		when(telemetryRepository.findByMachineId(1L)).thenReturn(List.of(t));
		assertEquals(1, telemetryService.findByMachineId(1L).size());
	}

	@Test
	void findByMachineId_shouldReturnEmptyList() {
		when(telemetryRepository.findByMachineId(999L)).thenReturn(List.of());
		assertEquals(0, telemetryService.findByMachineId(999L).size());
	}

	@Test
	void ingest_missingCertificate_shouldReject() {
		TelemetryRequest request = validRequest();
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				UnauthorizedException.class, () -> telemetryService.ingest(request, null));
	}

	@Test
	void ingest_unknownMachine_shouldReject() {
		TelemetryRequest request = validRequest();
		when(machineRepository.findByCode("VM-001")).thenReturn(Optional.empty());
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				ForbiddenOperationException.class,
				() -> telemetryService.ingest(request, "VM-001"));
	}

	@Test
	void ingest_inactiveMachine_shouldReject() {
		TelemetryRequest request = validRequest();
		when(machineRepository.findByCode("VM-001"))
				.thenReturn(Optional.of(activeMachine(false)));
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				ForbiddenOperationException.class,
				() -> telemetryService.ingest(request, "VM-001"));
	}

	@Test
	void ingest_identityMismatch_shouldReject() {
		TelemetryRequest request = validRequest();
		when(machineRepository.findByCode("VM-001"))
				.thenReturn(Optional.of(activeMachine(true)));
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				ForbiddenOperationException.class,
				() -> telemetryService.ingest(request, "VM-OTHER"));
	}

	@Test
	void ingest_rateLimitExceeded_shouldReject() {
		TelemetryRequest request = validRequest();
		when(machineRepository.findByCode("VM-001"))
				.thenReturn(Optional.of(activeMachine(true)));
		when(telemetryRepository.countByMachineIdAndTimestampAfter(eq(1L), any()))
				.thenReturn(2L);
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				RateLimitException.class, () -> telemetryService.ingest(request, "VM-001"));
	}

	@Test
	void ingest_validRequest_shouldAcceptAndRaiseAlerts() {
		TelemetryRequest request =
				TelemetryRequest.builder()
						.serialNumber("VM-001")
						.temperature(new BigDecimal("50.0"))
						.errorCodes(List.of("E01"))
						.stockLevels(Map.of("A1", 1))
						.statusCode("NOT_A_REAL_STATUS")
						.timestamp(LocalDateTime.now())
						.build();
		VendingMachine machine = activeMachine(true);
		when(machineRepository.findByCode("VM-001")).thenReturn(Optional.of(machine));
		when(telemetryRepository.countByMachineIdAndTimestampAfter(eq(1L), any()))
				.thenReturn(0L);
		when(telemetryRepository.save(any(MachineTelemetry.class)))
				.thenAnswer(
						inv -> {
							MachineTelemetry saved = inv.getArgument(0);
							saved.setId(99L);
							return saved;
						});
		when(machineRepository.save(any(VendingMachine.class))).thenReturn(machine);
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		var response = telemetryService.ingest(request, "VM-001");

		assertTrue(response.isAccepted());
		assertEquals(3, response.getAlertsRaised());
		assertEquals(MachineStatus.ONLINE, machine.getStatus());
		verify(machineRepository).save(machine);
	}

	@Test
	void save_certificateMismatch_shouldReject() {
		VendingMachine machine = VendingMachine.builder().code("VM-001").build();
		MachineTelemetry telemetry =
				MachineTelemetry.builder()
						.machine(machine)
						.status("ONLINE")
						.timestamp(LocalDateTime.now())
						.build();
		when(machineRepository.findByCode("VM-001"))
				.thenReturn(Optional.of(activeMachine(true)));
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		assertThrows(
				IllegalArgumentException.class,
				() -> telemetryService.save(telemetry, "VM-OTHER"));
	}

	@Test
	void save_withoutStatus_shouldDefaultMachineOnline() {
		VendingMachine machine = activeMachine(true);
		MachineTelemetry telemetry =
				MachineTelemetry.builder()
						.machine(VendingMachine.builder().code("VM-001").build())
						.timestamp(LocalDateTime.now())
						.build();
		when(machineRepository.findByCode("VM-001")).thenReturn(Optional.of(machine));
		when(machineRepository.save(any(VendingMachine.class))).thenReturn(machine);
		when(telemetryRepository.save(any(MachineTelemetry.class)))
				.thenAnswer(inv -> inv.getArgument(0));
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		telemetryService.save(telemetry, "VM-001");

		assertEquals(MachineStatus.ONLINE, machine.getStatus());
	}

	@Test
	void save_withExistingMachineId_shouldSkipLookup() {
		VendingMachine machine = activeMachine(true);
		MachineTelemetry telemetry =
				MachineTelemetry.builder()
						.machine(machine)
						.status("ONLINE")
						.timestamp(LocalDateTime.now())
						.build();
		when(telemetryRepository.save(any(MachineTelemetry.class)))
				.thenAnswer(inv -> inv.getArgument(0));
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		telemetryService.save(telemetry, "VM-001");

		verify(machineRepository, never()).findByCode(any());
	}

	private TelemetryRequest validRequest() {
		return TelemetryRequest.builder()
				.serialNumber("VM-001")
				.temperature(new BigDecimal("22.0"))
				.statusCode("ONLINE")
				.timestamp(LocalDateTime.now())
				.build();
	}

	private VendingMachine activeMachine(boolean active) {
		LocalDateTime now = LocalDateTime.now();
		return VendingMachine.builder()
				.id(1L)
				.code("VM-001")
				.location("Campus")
				.active(active)
				.status(MachineStatus.ONLINE)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}