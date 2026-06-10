package pt.isep.desofs.vendnet.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.desofs.vendnet.api.dto.PurchaseRequest;
import pt.isep.desofs.vendnet.api.dto.PurchaseResponse;
import pt.isep.desofs.vendnet.domain.exception.OutOfStockException;
import pt.isep.desofs.vendnet.domain.exception.PaymentDeclinedException;
import pt.isep.desofs.vendnet.domain.model.product.PaymentInfo;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.IdempotencyRecord;
import pt.isep.desofs.vendnet.domain.model.sale.PayStatus;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;
import pt.isep.desofs.vendnet.domain.repository.IdempotencyRepository;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.domain.repository.SaleRepository;
import pt.isep.desofs.vendnet.domain.repository.SlotRepository;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {

	private final SaleRepository saleRepository;
	private final SlotRepository slotRepository;
	private final ProductRepository productRepository;
	private final PaymentGatewayService paymentGatewayService;
	private final IdempotencyRepository idempotencyRepository;

	@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")
	public List<Sale> findByMachineId(Long machineId) {
		return saleRepository.findByMachineId(machineId);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	public List<Sale> findByUserId(Long userId) {
		return saleRepository.findByUserId(userId);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	@Transactional
	public PurchaseResponse purchase(PurchaseRequest request, Long userId) {

		Product product =
				productRepository
						.findById(request.getProductId())
						.orElseThrow(
								() ->
										new IllegalArgumentException(
												"Product not found: " + request.getProductId()));

		if (!product.isActive()) {
			throw new IllegalArgumentException("Product is not available");
		}

			String idempotencyKey = request.getIdempotencyKey();

		Optional<IdempotencyRecord> existing =
				idempotencyRepository.findByIdempotencyKey(idempotencyKey);
		if (existing.isPresent()) {
			log.info("Duplicate purchase request detected: key={}", idempotencyKey);
				return PurchaseResponse.builder()
						.saleId(String.valueOf(existing.get().getSaleId()))
						.status("DUPLICATE")
						.transactionRef(existing.get().getResponseBody())
						.message("Purchase already processed")
						.build();
			}

		Slot slot =
				slotRepository
						.findByMachineIdAndProductId(
								request.getMachineId(), request.getProductId())
						.orElseThrow(
								() ->
										new IllegalArgumentException(
												"Slot not found for product "
														+ request.getProductId()
														+ " in machine "
														+ request.getMachineId()));

		if (slot.getCurrentStock() <= 0) {
			throw new OutOfStockException("Product out of stock");
		}

		slot.reserveUnit();

		BigDecimal unitPrice = product.getPrice();
		BigDecimal totalAmount = unitPrice;

			PaymentInfo paymentInfo;
			try {
				paymentGatewayService.authorizePayment(request.getPaymentToken(), totalAmount);
				String transactionRef = "txn_" + UUID.randomUUID();
				paymentInfo =
						PaymentInfo.builder()
								.method("CARD")
								.transactionRef(transactionRef)
								.status(PayStatus.COMPLETED.name())
								.build();
			} catch (RuntimeException e) {
				String reason = e.getMessage() == null ? "Payment error" : e.getMessage();
				slot.releaseReservation();
				slot.setUpdatedAt(LocalDateTime.now());
				slotRepository.save(slot);

				LocalDateTime now = LocalDateTime.now();
			Sale failedSale =
					Sale.builder()
							.machine(slot.getMachine())
							.product(product)
							.userId(userId)
							.price(totalAmount)
							.quantity(1)
							.totalAmount(totalAmount)
							.unitPrice(unitPrice)
							.paymentInfo(
												PaymentInfo.builder()
												.method("CARD")
												.status(
														("GATEWAY_TIMEOUT".equals(reason) || "NETWORK_ERROR".equals(reason))
																? PayStatus.PENDING_VERIFICATION.name()
																: PayStatus.FAILED.name())
												.build())
							.saleDate(now)
							.createdAt(now)
							.build();
				Sale savedFailure = saleRepository.save(failedSale);

				if ("GATEWAY_TIMEOUT".equals(reason) || "NETWORK_ERROR".equals(reason)) {
					return PurchaseResponse.builder()
							.saleId(String.valueOf(savedFailure.getId()))
							.status("PENDING_VERIFICATION")
							.statusUrl("/api/sales/" + savedFailure.getId() + "/status")
							.message(
									"GATEWAY_TIMEOUT".equals(reason)
											? "Payment pending verification"
											: "Retrying payment with same idempotency key")
							.build();
				}

				throw new PaymentDeclinedException("Payment declined: " + reason);
			}

		slot.setUpdatedAt(LocalDateTime.now());
		slotRepository.save(slot);

		LocalDateTime now = LocalDateTime.now();
		Sale sale =
				Sale.builder()
						.machine(slot.getMachine())
						.product(product)
						.userId(userId)
						.price(totalAmount)
						.quantity(1)
						.totalAmount(totalAmount)
						.unitPrice(unitPrice)
						.paymentInfo(paymentInfo)
						.saleDate(now)
						.createdAt(now)
						.build();

		Sale saved = saleRepository.save(sale);

		idempotencyRepository.save(
				IdempotencyRecord.builder()
							.idempotencyKey(idempotencyKey)
							.responseStatus("COMPLETED")
							.responseBody(paymentInfo.getTransactionRef())
							.saleId(saved.getId())
						.createdAt(now)
						.build());

		log.info(
				"Purchase completed: saleId={}, product={}, machine={}, amount={}",
				saved.getId(),
				product.getSku(),
				slot.getMachine().getCode(),
				totalAmount);

		return PurchaseResponse.builder()
					.saleId(String.valueOf(saved.getId()))
					.status("COMPLETED")
					.transactionRef(paymentInfo.getTransactionRef())
					.message("Purchase completed successfully")
					.build();
	}
}
