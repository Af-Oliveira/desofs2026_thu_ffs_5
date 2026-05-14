package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;

@Repository
public interface JpaSaleRepository
		extends JpaRepository<Sale, Long>, pt.isep.desofs.vendnet.domain.repository.SaleRepository {

	List<Sale> findByMachineId(Long machineId);
}
