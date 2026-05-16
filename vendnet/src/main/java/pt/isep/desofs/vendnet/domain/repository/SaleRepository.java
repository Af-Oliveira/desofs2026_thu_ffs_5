package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

public interface SaleRepository {
	List<Sale> findByMachineId(Long machineId);

	List<Sale> findByUserId(Long userId);

	Sale save(Sale sale);
}
