package pt.isep.desofs.vendnet.domain.repository;

import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

import java.util.List;
import java.util.Optional;

public interface VendingMachineRepository {
    Optional<VendingMachine> findById(Long id);
    Optional<VendingMachine> findByCode(String code);
    List<VendingMachine> findAll();
    VendingMachine save(VendingMachine machine);
}
