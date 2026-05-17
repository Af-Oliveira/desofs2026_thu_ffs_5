package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
}