package pt.isep.desofs.vendnet.domain.model.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class VendingMachineTest {

	@Test
	void builder_shouldCreateMachineWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		VendingMachine machine = VendingMachine.builder()
				.id(1L)
				.code("VM-001")
				.location("Lisbon - Floor 1")
				.active(true)
				.createdAt(now)
				.updatedAt(now)
				.build();

		assertEquals(1L, machine.getId());
		assertEquals("VM-001", machine.getCode());
		assertEquals("Lisbon - Floor 1", machine.getLocation());
		assertTrue(machine.isActive());
		assertEquals(now, machine.getCreatedAt());
		assertEquals(now, machine.getUpdatedAt());
	}

	@Test
	void setters_shouldModifyFields() {
		VendingMachine machine = new VendingMachine();
		LocalDateTime now = LocalDateTime.now();

		machine.setId(2L);
		machine.setCode("VM-002");
		machine.setLocation("Porto - Floor 2");
		machine.setActive(false);
		machine.setCreatedAt(now);
		machine.setUpdatedAt(now);

		assertEquals(2L, machine.getId());
		assertEquals("VM-002", machine.getCode());
		assertEquals("Porto - Floor 2", machine.getLocation());
		assertFalse(machine.isActive());
		assertEquals(now, machine.getCreatedAt());
		assertEquals(now, machine.getUpdatedAt());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyMachine() {
		VendingMachine machine = new VendingMachine();

		assertNull(machine.getId());
		assertNull(machine.getCode());
		assertNull(machine.getLocation());
		assertFalse(machine.isActive());
		assertNull(machine.getCreatedAt());
		assertNull(machine.getUpdatedAt());
	}

	@Test
	void allArgsConstructor_shouldCreateMachine() {
		LocalDateTime now = LocalDateTime.now();

		VendingMachine machine = new VendingMachine(
				3L, "VM-003", "Aveiro - Floor 3", true, now, now);

		assertEquals(3L, machine.getId());
		assertEquals("VM-003", machine.getCode());
		assertEquals("Aveiro - Floor 3", machine.getLocation());
		assertTrue(machine.isActive());
		assertEquals(now, machine.getCreatedAt());
		assertEquals(now, machine.getUpdatedAt());
	}

	@Test
	void activeFlag_shouldBeTogglable() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder()
				.code("VM-004")
				.location("Test")
				.active(false)
				.createdAt(now)
				.updatedAt(now)
				.build();

		assertFalse(machine.isActive());

		machine.setActive(true);
		assertTrue(machine.isActive());
	}
}
