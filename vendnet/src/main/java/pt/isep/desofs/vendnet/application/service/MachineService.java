package pt.isep.desofs.vendnet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineService {

    private final VendingMachineRepository machineRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<VendingMachine> findAll() {
        return machineRepository.findAll();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public VendingMachine findByCode(String code) {
        return machineRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + code));
    }
}
