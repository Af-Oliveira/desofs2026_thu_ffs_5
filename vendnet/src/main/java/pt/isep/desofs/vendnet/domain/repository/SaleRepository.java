package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

public interface SaleRepository {
	List<Sale> findByMachineId(Long machineId);

<<<<<<< Updated upstream
	List<Sale> findByUserId(Long userId);
=======
	List<Sale> findByCustomerEmailOrderBySaleDateDesc(String customerEmail);
>>>>>>> Stashed changes

	Sale save(Sale sale);
}
