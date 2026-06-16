package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.isep.desofs.vendnet.domain.model.machine.MachineStatus;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@ExtendWith(MockitoExtension.class)
class MachineServiceTest {

	@Mock private VendingMachineRepository machineRepository;

	private MachineService machineService;

	@BeforeEach
	void setUp() {
		machineService = new MachineService(machineRepository);
	}

	@Test
	void findAll_shouldReturnMachines() {
		VendingMachine m = buildMachine("VM-001", "Lisbon");
		when(machineRepository.findAll()).thenReturn(java.util.List.of(m));
		assertEquals(1, machineService.findAll().size());
	}

	@Test
	void findAll_shouldReturnEmptyList() {
		when(machineRepository.findAll()).thenReturn(Collections.emptyList());
		assertTrue(machineService.findAll().isEmpty());
	}

	@Test
	void findByCode_shouldReturnMachine() {
		VendingMachine m = buildMachine("VM-001", "Lisbon");
		when(machineRepository.findByCode("VM-001")).thenReturn(Optional.of(m));
		assertEquals("VM-001", machineService.findByCode("VM-001").getCode());
	}

	@Test
	void findByCode_shouldThrowWhenNotFound() {
		when(machineRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> machineService.findByCode("UNKNOWN"));
	}

	@Test
	void createMachine_shouldSaveAndReturn() {
		when(machineRepository.findByCode("VM-NEW")).thenReturn(Optional.empty());
		when(machineRepository.save(any(VendingMachine.class)))
				.thenAnswer(
						inv -> {
							VendingMachine m = inv.getArgument(0);
							m.setId(1L);
							return m;
						});
		VendingMachine result = machineService.createMachine("VM-NEW", "Porto");
		assertNotNull(result);
		assertEquals("VM-NEW", result.getCode());
		assertEquals("Porto", result.getLocation());
		assertTrue(result.isActive());
	}

	@Test
	void createMachine_shouldThrowWhenDuplicateCode() {
		when(machineRepository.findByCode("VM-001"))
				.thenReturn(Optional.of(buildMachine("VM-001", "Lisbon")));
		assertThrows(
				IllegalArgumentException.class,
				() -> machineService.createMachine("VM-001", "Lisbon"));
		verify(machineRepository, never()).save(any());
	}

	@Test
	void updateMachine_shouldUpdateFields() {
		VendingMachine existing = buildMachine("VM-001", "Lisbon");
		existing.setId(1L);
		when(machineRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(machineRepository.save(any(VendingMachine.class)))
				.thenAnswer(inv -> inv.getArgument(0));
		VendingMachine result = machineService.updateMachine(1L, "VM-002", "Porto", false);
		assertEquals("VM-002", result.getCode());
		assertEquals("Porto", result.getLocation());
		assertEquals(false, result.isActive());
	}

	@Test
	void updateMachine_shouldPartialUpdate() {
		VendingMachine existing = buildMachine("VM-001", "Lisbon");
		existing.setId(1L);
		when(machineRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(machineRepository.save(any(VendingMachine.class)))
				.thenAnswer(inv -> inv.getArgument(0));
		VendingMachine result = machineService.updateMachine(1L, null, "Porto", null);
		assertEquals("Porto", result.getLocation());
		assertEquals("VM-001", result.getCode());
		assertEquals(true, result.isActive());
	}

	@Test
	void updateMachine_shouldThrowWhenNotFound() {
		when(machineRepository.findById(999L)).thenReturn(Optional.empty());
		assertThrows(
				IllegalArgumentException.class,
				() -> machineService.updateMachine(999L, null, null, null));
	}

	private VendingMachine buildMachine(String code, String location) {
		LocalDateTime now = LocalDateTime.now();
		return VendingMachine.builder()
				.id(1L)
				.code(code)
				.location(location)
				.active(true)
				.status(MachineStatus.ONLINE)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}
