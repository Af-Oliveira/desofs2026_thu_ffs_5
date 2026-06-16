package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.isep.desofs.vendnet.domain.model.machine.MachineStatus;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.exception.CapacityExceededException;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

	@Mock
	private SlotRepository slotRepository;

	@Mock
	private VendingMachineRepository machineRepository;

	@Mock
	private AuditLogRepository auditLogRepository;

	private SlotService slotService;

	@BeforeEach
	void setUp() {
		slotService = new SlotService(slotRepository, machineRepository, auditLogRepository);
	}

	@Test
	void findByMachineId_shouldReturnListOfSlots() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		Slot slot = Slot.builder()
				.id(1L).position("A1").capacity(20).currentStock(15)
				.machine(machine).product(product)
				.createdAt(now).updatedAt(now).build();
		when(slotRepository.findByMachineId(1L)).thenReturn(List.of(slot));

		List<Slot> result = slotService.findByMachineId(1L);

		assertEquals(1, result.size());
		assertEquals("A1", result.get(0).getPosition());
		assertEquals(15, result.get(0).getCurrentStock());
	}

	@Test
	void findByMachineId_shouldReturnEmptyList() {
		when(slotRepository.findByMachineId(999L)).thenReturn(Collections.emptyList());

		List<Slot> result = slotService.findByMachineId(999L);

		assertTrue(result.isEmpty());
	}

	@Test
	void restock_shouldUpdateStock() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001")
				.status(MachineStatus.ONLINE).build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		Slot slot = Slot.builder()
				.id(1L).position("A1").capacity(20).currentStock(10)
				.machine(machine).product(product)
				.createdAt(now).updatedAt(now).build();

		when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
		when(slotRepository.findByMachineIdAndId(1L, 1L)).thenReturn(Optional.of(slot));
		when(slotRepository.save(slot)).thenReturn(slot);

		Slot result = slotService.restock(1L, 1L, 5, 100L);

		assertEquals(15, result.getCurrentStock());
	}

	@Test
	void restock_machineOffline_shouldThrowException() {
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001")
				.status(MachineStatus.OFFLINE).build();

		when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));

		assertThrows(pt.isep.desofs.vendnet.domain.exception.MachineOfflineException.class,
				() -> slotService.restock(1L, 1L, 5, 100L));
	}

	@Test
	void restock_exceedsCapacity_shouldThrowException() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001")
				.status(MachineStatus.ONLINE).build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		Slot slot = Slot.builder()
				.id(1L).position("A1").capacity(20).currentStock(18)
				.machine(machine).product(product)
				.createdAt(now).updatedAt(now).build();

		when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
		when(slotRepository.findByMachineIdAndId(1L, 1L)).thenReturn(Optional.of(slot));

		assertThrows(CapacityExceededException.class,
				() -> slotService.restock(1L, 1L, 5, 100L));
	}

	@Test
	void restock_machineNotFound_shouldThrowException() {
		when(machineRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> slotService.restock(999L, 1L, 5, 100L));
	}

	@Test
	void restock_slotNotFound_shouldThrowException() {
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001")
				.status(MachineStatus.ONLINE).build();
		when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
		when(slotRepository.findByMachineIdAndId(1L, 999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> slotService.restock(1L, 999L, 5, 100L));
	}
}
