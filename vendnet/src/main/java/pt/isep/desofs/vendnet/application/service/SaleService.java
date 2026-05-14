package pt.isep.desofs.vendnet.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {

	private final SaleRepository saleRepository;
	private final PaymentGatewayService paymentGatewayService;

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<Sale> findByMachineId(Long machineId) {
		return saleRepository.findByMachineId(machineId);
	}
}
