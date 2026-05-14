package pt.isep.desofs.vendnet.domain.model.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

class MachineTelemetryTest {

	@Test
	void builder_shouldCreateTelemetryWithAllFields() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();

		MachineTelemetry telemetry = MachineTelemetry.builder()
				.id(1L)
				.machine(machine)
				.cpuUsage(new BigDecimal("45.5"))
				.memoryUsage(new BigDecimal("60.2"))
				.diskUsage(new BigDecimal("30.0"))
				.status("ONLINE")
				.uptimeSeconds(3600L)
				.totalSalesToday(50)
				.temperatureCelsius(new BigDecimal("22.5"))
				.timestamp(now)
				.build();

		assertEquals(1L, telemetry.getId());
		assertEquals(machine, telemetry.getMachine());
		assertEquals(new BigDecimal("45.5"), telemetry.getCpuUsage());
		assertEquals(new BigDecimal("60.2"), telemetry.getMemoryUsage());
		assertEquals(new BigDecimal("30.0"), telemetry.getDiskUsage());
		assertEquals("ONLINE", telemetry.getStatus());
		assertEquals(3600L, telemetry.getUptimeSeconds());
		assertEquals(50, telemetry.getTotalSalesToday());
		assertEquals(new BigDecimal("22.5"), telemetry.getTemperatureCelsius());
		assertEquals(now, telemetry.getTimestamp());
	}

	@Test
	void setters_shouldModifyFields() {
		MachineTelemetry telemetry = new MachineTelemetry();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		LocalDateTime now = LocalDateTime.now();

		telemetry.setId(2L);
		telemetry.setMachine(machine);
		telemetry.setCpuUsage(new BigDecimal("80.0"));
		telemetry.setMemoryUsage(new BigDecimal("90.0"));
		telemetry.setDiskUsage(new BigDecimal("70.0"));
		telemetry.setStatus("MAINTENANCE");
		telemetry.setUptimeSeconds(7200L);
		telemetry.setTotalSalesToday(100);
		telemetry.setTemperatureCelsius(new BigDecimal("25.0"));
		telemetry.setTimestamp(now);

		assertEquals(2L, telemetry.getId());
		assertNotNull(telemetry.getMachine());
		assertEquals(new BigDecimal("80.0"), telemetry.getCpuUsage());
		assertEquals(new BigDecimal("90.0"), telemetry.getMemoryUsage());
		assertEquals(new BigDecimal("70.0"), telemetry.getDiskUsage());
		assertEquals("MAINTENANCE", telemetry.getStatus());
		assertEquals(7200L, telemetry.getUptimeSeconds());
		assertEquals(100, telemetry.getTotalSalesToday());
		assertEquals(new BigDecimal("25.0"), telemetry.getTemperatureCelsius());
		assertEquals(now, telemetry.getTimestamp());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyTelemetry() {
		MachineTelemetry telemetry = new MachineTelemetry();

		assertNull(telemetry.getId());
		assertNull(telemetry.getMachine());
		assertNull(telemetry.getCpuUsage());
		assertNull(telemetry.getMemoryUsage());
		assertNull(telemetry.getDiskUsage());
		assertNull(telemetry.getStatus());
		assertNull(telemetry.getUptimeSeconds());
		assertNull(telemetry.getTotalSalesToday());
		assertNull(telemetry.getTemperatureCelsius());
		assertNull(telemetry.getTimestamp());
	}

	@Test
	void allArgsConstructor_shouldCreateTelemetry() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();

		MachineTelemetry telemetry = new MachineTelemetry(
				3L, machine, new BigDecimal("10.0"), new BigDecimal("20.0"),
				new BigDecimal("15.0"), "ONLINE", 1000L, 25,
				new BigDecimal("21.0"), now);

		assertEquals(3L, telemetry.getId());
		assertEquals(machine, telemetry.getMachine());
		assertEquals(new BigDecimal("10.0"), telemetry.getCpuUsage());
		assertEquals(new BigDecimal("20.0"), telemetry.getMemoryUsage());
		assertEquals(new BigDecimal("15.0"), telemetry.getDiskUsage());
		assertEquals("ONLINE", telemetry.getStatus());
		assertEquals(1000L, telemetry.getUptimeSeconds());
		assertEquals(25, telemetry.getTotalSalesToday());
		assertEquals(new BigDecimal("21.0"), telemetry.getTemperatureCelsius());
		assertEquals(now, telemetry.getTimestamp());
	}
}
