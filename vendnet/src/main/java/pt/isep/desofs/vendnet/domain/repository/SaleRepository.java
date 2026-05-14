package pt.isep.desofs.vendnet.domain.repository;

import pt.isep.desofs.vendnet.domain.model.sale.Sale;

import java.util.List;

public interface SaleRepository {
    List<Sale> findByMachineId(Long machineId);
    Sale save(Sale sale);
}
