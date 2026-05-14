package pt.isep.desofs.vendnet.application.service;

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
}
