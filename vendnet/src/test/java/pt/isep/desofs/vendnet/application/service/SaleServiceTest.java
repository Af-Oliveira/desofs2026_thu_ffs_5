package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

	@Mock
	private SaleRepository saleRepository;

	@Mock
	private PaymentGatewayService paymentGatewayService;

	private SaleService saleService;

	@BeforeEach
	void setUp() {
		saleService = new SaleService(saleRepository, paymentGatewayService);
	}

	@Test
	void findByMachineId_shouldReturnListOfSales() {
		LocalDateTime now = LocalDateTime.now();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
		Product product = Product.builder().id(1L).sku("SKU-001").build();
		Sale sale = Sale.builder()
				.id(1L)
				.machine(machine)
				.product(product)
				.price(new BigDecimal("2.50"))
				.quantity(2)
				.saleDate(now)
				.createdAt(now)
				.build();
		when(saleRepository.findByMachineId(1L)).thenReturn(List.of(sale));

		List<Sale> result = saleService.findByMachineId(1L);

		assertEquals(1, result.size());
		assertEquals(new BigDecimal("2.50"), result.get(0).getPrice());
		assertEquals(2, result.get(0).getQuantity());
	}

	@Test
	void findByMachineId_shouldReturnEmptyList() {
		when(saleRepository.findByMachineId(999L)).thenReturn(Collections.emptyList());

		List<Sale> result = saleService.findByMachineId(999L);

		assertTrue(result.isEmpty());
	}
}
