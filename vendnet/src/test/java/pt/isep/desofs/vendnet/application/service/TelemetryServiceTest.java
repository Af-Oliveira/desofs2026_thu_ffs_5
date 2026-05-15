package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.telemetry.MachineTelemetry;
import pt.isep.desofs.vendnet.domain.repository.TelemetryRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

	@Mock
	private TelemetryRepository telemetryRepository;

	@Mock
	private VendingMachineRepository machineRepository;

	private TelemetryService telemetryService;

	@BeforeEach
	void setUp() {
		telemetryService = new TelemetryService(telemetryRepository, machineRepository);
	}

	@Test
	void save_shouldSaveAndReturnTelemetry() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		MachineTelemetry telemetry = MachineTelemetry.builder()
				.machine(machine)
				.cpuUsage(new BigDecimal("45.5"))
				.memoryUsage(new BigDecimal("60.0"))
				.status("ONLINE")
				.timestamp(now)
				.build();
		when(telemetryRepository.save(any(MachineTelemetry.class))).thenReturn(telemetry);

		MachineTelemetry result = telemetryService.save(telemetry);

		assertEquals(new BigDecimal("45.5"), result.getCpuUsage());
		assertEquals("ONLINE", result.getStatus());
		verify(telemetryRepository).save(telemetry);
	}

	@Test
	void findByMachineId_shouldReturnList() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		MachineTelemetry telemetry = MachineTelemetry.builder()
				.id(1L)
				.machine(machine)
				.cpuUsage(new BigDecimal("80.0"))
				.status("HIGH_LOAD")
				.timestamp(now)
				.build();
		when(telemetryRepository.findByMachineId(1L)).thenReturn(List.of(telemetry));

		List<MachineTelemetry> result = telemetryService.findByMachineId(1L);

		assertEquals(1, result.size());
		assertEquals("HIGH_LOAD", result.get(0).getStatus());
	}

	@Test
	void findByMachineId_shouldReturnEmptyList() {
		when(telemetryRepository.findByMachineId(999L)).thenReturn(Collections.emptyList());

		List<MachineTelemetry> result = telemetryService.findByMachineId(999L);

		assertTrue(result.isEmpty());
	}
}
