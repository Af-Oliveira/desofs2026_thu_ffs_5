package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;

@Repository
public interface JpaVendingMachineRepository
		extends JpaRepository<VendingMachine, Long>,
				pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository {

	Optional<VendingMachine> findByCode(String code);
}
