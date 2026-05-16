package pt.isep.desofs.vendnet.application.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.desofs.vendnet.api.dto.CustomerPurchaseRequest;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;

@Service
@RequiredArgsConstructor
public class CustomerPurchaseService {

	private final SlotRepository slotRepository;
	private final SaleRepository saleRepository;

	@Transactional
	@PreAuthorize("hasRole('CUSTOMER')")
	public Sale purchase(String customerEmail, CustomerPurchaseRequest request) {
		List<Slot> locked =
				slotRepository.lockSlotsForPurchase(request.getMachineId(), request.getProductSku());
		Slot slot =
				locked.stream()
						.filter(s -> s.getCurrentStock() >= request.getQuantity())
						.findFirst()
						.orElseThrow(
								() ->
										new IllegalArgumentException(
												"Insufficient stock or no slot for this machine and SKU"));

		Hibernate.initialize(slot.getMachine());
		Hibernate.initialize(slot.getProduct());

		int newStock = slot.getCurrentStock() - request.getQuantity();
		slot.setCurrentStock(newStock);
		slot.setUpdatedAt(LocalDateTime.now());
		slotRepository.save(slot);

		LocalDateTime now = LocalDateTime.now();
		Sale sale =
				Sale.builder()
						.machine(slot.getMachine())
						.product(slot.getProduct())
						.price(slot.getProduct().getPrice())
						.quantity(request.getQuantity())
						.customerEmail(customerEmail)
						.saleDate(now)
						.createdAt(now)
						.build();
		Sale saved = saleRepository.save(sale);
		Hibernate.initialize(saved.getMachine());
		Hibernate.initialize(saved.getProduct());
		return saved;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<Sale> findMyPurchases(String customerEmail) {
		List<Sale> list = saleRepository.findByCustomerEmailOrderBySaleDateDesc(customerEmail);
		for (Sale s : list) {
			Hibernate.initialize(s.getMachine());
			Hibernate.initialize(s.getProduct());
		}
		return list;
	}
}
