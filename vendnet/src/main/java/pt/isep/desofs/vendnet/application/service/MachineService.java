package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineService {

	private final VendingMachineRepository machineRepository;

	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public List<VendingMachine> findAll() {
		return machineRepository.findAll();
	}

	@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
	public VendingMachine findByCode(String code) {
		return machineRepository
				.findByCode(code)
				.orElseThrow(() -> new IllegalArgumentException("Machine not found: " + code));
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public VendingMachine createMachine(String code, String location) {
		if (machineRepository.findByCode(code).isPresent()) {
			throw new IllegalArgumentException("Machine with code '" + code + "' already exists");
		}

		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine =
				VendingMachine.builder()
						.code(code)
						.location(location)
						.active(true)
						.createdAt(now)
						.updatedAt(now)
						.build();

		return machineRepository.save(machine);
	}

	@PreAuthorize("hasRole('ADMINISTRATOR')")
	public VendingMachine updateMachine(Long id, String code, String location, Boolean active) {
		VendingMachine existing =
				machineRepository
						.findById(id)
						.orElseThrow(
								() -> new IllegalArgumentException("Machine not found: " + id));

		if (code != null) {
			existing.setCode(code);
		}
		if (location != null) {
			existing.setLocation(location);
		}
		if (active != null) {
			existing.setActive(active);
		}
		existing.setUpdatedAt(LocalDateTime.now());

		return machineRepository.save(existing);
	}
}
