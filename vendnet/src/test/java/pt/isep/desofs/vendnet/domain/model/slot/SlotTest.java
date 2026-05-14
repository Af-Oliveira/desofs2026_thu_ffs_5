package pt.isep.desofs.vendnet.domain.model.slot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;

class SlotTest {

	@Test
	void builder_shouldCreateSlotWithAllFields() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();

		Slot slot = Slot.builder()
				.id(1L)
				.position("A1")
				.capacity(20)
				.currentStock(15)
				.machine(machine)
				.product(product)
				.createdAt(now)
				.updatedAt(now)
				.build();

		assertEquals(1L, slot.getId());
		assertEquals("A1", slot.getPosition());
		assertEquals(20, slot.getCapacity());
		assertEquals(15, slot.getCurrentStock());
		assertEquals(machine, slot.getMachine());
		assertEquals(product, slot.getProduct());
		assertEquals(now, slot.getCreatedAt());
		assertEquals(now, slot.getUpdatedAt());
	}

	@Test
	void setters_shouldModifyFields() {
		Slot slot = new Slot();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		LocalDateTime now = LocalDateTime.now();

		slot.setId(2L);
		slot.setPosition("B3");
		slot.setCapacity(30);
		slot.setCurrentStock(10);
		slot.setMachine(machine);
		slot.setProduct(product);
		slot.setCreatedAt(now);
		slot.setUpdatedAt(now);

		assertEquals(2L, slot.getId());
		assertEquals("B3", slot.getPosition());
		assertEquals(30, slot.getCapacity());
		assertEquals(10, slot.getCurrentStock());
		assertNotNull(slot.getMachine());
		assertNotNull(slot.getProduct());
		assertEquals(now, slot.getCreatedAt());
		assertEquals(now, slot.getUpdatedAt());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptySlot() {
		Slot slot = new Slot();

		assertNull(slot.getId());
		assertNull(slot.getPosition());
		assertEquals(0, slot.getCapacity());
		assertEquals(0, slot.getCurrentStock());
		assertNull(slot.getMachine());
		assertNull(slot.getProduct());
		assertNull(slot.getCreatedAt());
		assertNull(slot.getUpdatedAt());
	}

	@Test
	void allArgsConstructor_shouldCreateSlot() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();

		Slot slot = new Slot(3L, "C5", 40, 35, machine, product, now, now);

		assertEquals(3L, slot.getId());
		assertEquals("C5", slot.getPosition());
		assertEquals(40, slot.getCapacity());
		assertEquals(35, slot.getCurrentStock());
		assertEquals(machine, slot.getMachine());
		assertEquals(product, slot.getProduct());
		assertEquals(now, slot.getCreatedAt());
		assertEquals(now, slot.getUpdatedAt());
	}
}
