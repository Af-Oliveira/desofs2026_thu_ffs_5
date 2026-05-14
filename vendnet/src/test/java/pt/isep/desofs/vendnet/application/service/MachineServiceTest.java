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
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
class MachineServiceTest {

	@Mock
	private VendingMachineRepository machineRepository;

	private MachineService machineService;

	@BeforeEach
	void setUp() {
		machineService = new MachineService(machineRepository);
	}

	@Test
	void findAll_shouldReturnListOfMachines() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder()
				.id(1L)
				.code("VM-001")
				.location("Lisbon")
				.active(true)
				.createdAt(now)
				.updatedAt(now)
				.build();
		when(machineRepository.findAll()).thenReturn(List.of(machine));

		List<VendingMachine> result = machineService.findAll();

		assertEquals(1, result.size());
		assertEquals("VM-001", result.get(0).getCode());
	}

	@Test
	void findAll_shouldReturnEmptyList() {
		when(machineRepository.findAll()).thenReturn(Collections.emptyList());

		List<VendingMachine> result = machineService.findAll();

		assertTrue(result.isEmpty());
	}

	@Test
	void findByCode_shouldReturnMachine() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder()
				.id(1L)
				.code("VM-002")
				.location("Porto")
				.active(true)
				.createdAt(now)
				.updatedAt(now)
				.build();
		when(machineRepository.findByCode("VM-002")).thenReturn(Optional.of(machine));

		VendingMachine result = machineService.findByCode("VM-002");

		assertEquals("VM-002", result.getCode());
		assertEquals("Porto", result.getLocation());
	}

	@Test
	void findByCode_shouldThrowWhenNotFound() {
		when(machineRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> machineService.findByCode("UNKNOWN"));
	}
}
