package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.isep.desofs.vendnet.api.dto.PurchaseRequest;
import pt.isep.desofs.vendnet.api.dto.PurchaseResponse;
import pt.isep.desofs.vendnet.domain.exception.OutOfStockException;
import pt.isep.desofs.vendnet.domain.exception.PaymentDeclinedException;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.IdempotencyRecord;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.repository.IdempotencyRepository;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayException;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

	@Mock private SaleRepository saleRepository;
	@Mock private SlotRepository slotRepository;
	@Mock private ProductRepository productRepository;
	@Mock private PaymentGatewayService paymentGatewayService;
	@Mock private IdempotencyRepository idempotencyRepository;

	private SaleService saleService;

	@BeforeEach
	void setUp() {
		saleService = new SaleService(saleRepository, slotRepository, productRepository, paymentGatewayService, idempotencyRepository);
	}

	@Test
	void findByMachineId_shouldReturnSales() {
		when(saleRepository.findByMachineId(1L)).thenReturn(java.util.List.of());
		assertEquals(0, saleService.findByMachineId(1L).size());
	}

	@Test
	void findByUserId_shouldReturnSales() {
		when(saleRepository.findByUserId(1L)).thenReturn(java.util.List.of());
		assertEquals(0, saleService.findByUserId(1L).size());
	}

	@Test
	void purchase_shouldCompleteSuccessfully() {
		Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(true).build();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
			Slot slot = Slot.builder().id(1L).position("A1").capacity(20).currentStock(10).product(product).machine(machine).build();
			PurchaseRequest request = PurchaseRequest.builder()
					.productId(1L).machineId(1L).paymentToken("tok-123").idempotencyKey("key-123").build();
			when(productRepository.findById(1L)).thenReturn(Optional.of(product));
			when(idempotencyRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
			when(slotRepository.findByMachineIdAndProductId(1L, 1L)).thenReturn(List.of(slot));
		when(slotRepository.save(any(Slot.class))).thenAnswer(inv -> inv.getArgument(0));
		when(saleRepository.save(any())).thenAnswer(inv -> {
			pt.isep.desofs.vendnet.domain.model.sale.Sale s = inv.getArgument(0);
			s.setId(1L);
			return s;
		});
		when(idempotencyRepository.save(any(IdempotencyRecord.class))).thenAnswer(inv -> inv.getArgument(0));
		PurchaseResponse response = saleService.purchase(request, 1L);
		assertEquals("COMPLETED", response.getStatus());
		assertNotNull(response.getSaleId());
	}

	@Test
	void purchase_shouldThrowWhenProductNotFound() {
		PurchaseRequest request = PurchaseRequest.builder()
				.productId(999L).machineId(1L).paymentToken("tok").build();
		when(productRepository.findById(999L)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> saleService.purchase(request, 1L));
	}

	@Test
	void purchase_shouldThrowWhenProductInactive() {
		Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(false).build();
		PurchaseRequest request = PurchaseRequest.builder()
				.productId(1L).machineId(1L).paymentToken("tok").build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		assertThrows(IllegalArgumentException.class, () -> saleService.purchase(request, 1L));
	}

	@Test
	void purchase_shouldThrowWhenOutOfStock() {
		Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(true).build();
		VendingMachine machine = VendingMachine.builder().id(1L).build();
			Slot slot = Slot.builder().id(1L).capacity(20).currentStock(0).product(product).machine(machine).build();
			PurchaseRequest request = PurchaseRequest.builder()
					.productId(1L).machineId(1L).paymentToken("tok").idempotencyKey("key-empty").build();
			when(productRepository.findById(1L)).thenReturn(Optional.of(product));
			when(idempotencyRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
			when(slotRepository.findByMachineIdAndProductId(1L, 1L)).thenReturn(List.of(slot));
		assertThrows(OutOfStockException.class, () -> saleService.purchase(request, 1L));
	}

	@Test
	void purchase_shouldThrowPaymentDeclined() {
		Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(true).build();
		VendingMachine machine = VendingMachine.builder().id(1L).code("VM-001").build();
			Slot slot = Slot.builder().id(1L).capacity(20).currentStock(10).product(product).machine(machine).build();
			PurchaseRequest request = PurchaseRequest.builder()
					.productId(1L).machineId(1L).paymentToken("tok-fail").idempotencyKey("key-fail").build();
			when(productRepository.findById(1L)).thenReturn(Optional.of(product));
			when(idempotencyRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
			when(slotRepository.findByMachineIdAndProductId(1L, 1L)).thenReturn(List.of(slot));
		when(slotRepository.save(any(Slot.class))).thenAnswer(inv -> inv.getArgument(0));
		when(saleRepository.save(any())).thenAnswer(inv -> {
			pt.isep.desofs.vendnet.domain.model.sale.Sale s = inv.getArgument(0);
			s.setId(2L);
			return s;
		});
		doThrow(new PaymentGatewayException("Card declined")).when(paymentGatewayService).authorizePayment(anyString(), any(BigDecimal.class));
		assertThrows(PaymentDeclinedException.class, () -> saleService.purchase(request, 1L));
	}

	@Test
	void purchase_shouldThrowWhenSlotNotFound() {
			Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(true).build();
			PurchaseRequest request = PurchaseRequest.builder()
					.productId(1L).machineId(1L).paymentToken("tok").idempotencyKey("key-missing-slot").build();
			when(productRepository.findById(1L)).thenReturn(Optional.of(product));
			when(idempotencyRepository.findByIdempotencyKey(any())).thenReturn(Optional.empty());
			when(slotRepository.findByMachineIdAndProductId(1L, 1L)).thenReturn(List.of());
		assertThrows(IllegalArgumentException.class, () -> saleService.purchase(request, 1L));
	}

	@Test
	void purchase_shouldReturnDuplicateForIdempotentRequest() {
		Product product = Product.builder().id(1L).sku("SKU-001").price(new BigDecimal("1.50")).active(true).build();
		PurchaseRequest request = PurchaseRequest.builder()
				.productId(1L).machineId(1L).paymentToken("tok").idempotencyKey("key-123").build();
		IdempotencyRecord idempotencyRecord =
				IdempotencyRecord.builder().idempotencyKey("key-123").responseStatus("COMPLETED").saleId(1L).build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(idempotencyRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.of(idempotencyRecord));
		PurchaseResponse response = saleService.purchase(request, 1L);
		assertEquals("DUPLICATE", response.getStatus());
	}
}
