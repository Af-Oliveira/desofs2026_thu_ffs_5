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
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayException;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {

	private static final String CARD_PAYMENT_METHOD = "CARD";
	private static final String COMPLETED_STATUS = "COMPLETED";
	private static final String GATEWAY_TIMEOUT = "GATEWAY_TIMEOUT";
	private static final String NETWORK_ERROR = "NETWORK_ERROR";

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
		Product product = findAvailableProduct(request.getProductId());
		String idempotencyKey = request.getIdempotencyKey();

		Optional<PurchaseResponse> duplicateResponse = replayIfDuplicate(idempotencyKey);
		if (duplicateResponse.isPresent()) {
			return duplicateResponse.get();
		}

		Slot slot = findAvailableSlot(request);
		slot.reserveUnit();

		BigDecimal unitPrice = product.getPrice();
		BigDecimal totalAmount = unitPrice;
		PaymentAuthorization authorization =
				authorizePayment(request, slot, product, userId, unitPrice, totalAmount);
		if (authorization.pendingResponse() != null) {
			return authorization.pendingResponse();
		}
		PaymentInfo paymentInfo = authorization.paymentInfo();

		slot.setUpdatedAt(LocalDateTime.now());
		slotRepository.save(slot);

		LocalDateTime now = LocalDateTime.now();
		Sale saved = saleRepository.save(buildSale(slot, product, userId, totalAmount, unitPrice, paymentInfo, now));

		idempotencyRepository.save(
				IdempotencyRecord.builder()
						.idempotencyKey(idempotencyKey)
						.responseStatus(COMPLETED_STATUS)
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
				.status(COMPLETED_STATUS)
				.transactionRef(paymentInfo.getTransactionRef())
				.message("Purchase completed successfully")
				.build();
	}

	private Product findAvailableProduct(Long productId) {
		Product product =
				productRepository
						.findById(productId)
						.orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

		if (!product.isActive()) {
			throw new IllegalArgumentException("Product is not available");
		}
		return product;
	}

	private Optional<PurchaseResponse> replayIfDuplicate(String idempotencyKey) {
		Optional<IdempotencyRecord> existing =
				idempotencyRepository.findByIdempotencyKey(idempotencyKey);
		if (existing.isEmpty()) {
			return Optional.empty();
		}

		IdempotencyRecord record = existing.get();
		log.info("Duplicate purchase request detected: key={}", idempotencyKey);
		return Optional.of(
				PurchaseResponse.builder()
						.saleId(String.valueOf(record.getSaleId()))
						.status("DUPLICATE")
						.transactionRef(record.getResponseBody())
						.message("Purchase already processed")
						.build());
	}

	private Slot findAvailableSlot(PurchaseRequest request) {
		List<Slot> slots =
				slotRepository.findByMachineIdAndProductId(request.getMachineId(), request.getProductId());
		if (slots.isEmpty()) {
			throw new IllegalArgumentException(
					"Slot not found for product "
							+ request.getProductId()
							+ " in machine "
							+ request.getMachineId());
		}

		return slots.stream()
				.filter(candidate -> candidate.getCurrentStock() > 0)
				.findFirst()
				.orElseThrow(() -> new OutOfStockException("Product out of stock"));
	}

	private PaymentAuthorization authorizePayment(
			PurchaseRequest request,
			Slot slot,
			Product product,
			Long userId,
			BigDecimal unitPrice,
			BigDecimal totalAmount) {
		try {
			paymentGatewayService.authorizePayment(request.getPaymentToken(), totalAmount);
			return new PaymentAuthorization(
					PaymentInfo.builder()
							.method(CARD_PAYMENT_METHOD)
							.transactionRef("txn_" + UUID.randomUUID())
							.status(PayStatus.COMPLETED.name())
							.build(),
					null);
		} catch (PaymentGatewayException e) {
			return handlePaymentFailure(slot, product, userId, unitPrice, totalAmount, e);
		}
	}

	private PaymentAuthorization handlePaymentFailure(
			Slot slot,
			Product product,
			Long userId,
			BigDecimal unitPrice,
			BigDecimal totalAmount,
			PaymentGatewayException exception) {
		String reason = exception.getMessage() == null ? "Payment error" : exception.getMessage();
		slot.releaseReservation();
		slot.setUpdatedAt(LocalDateTime.now());
		slotRepository.save(slot);

		LocalDateTime now = LocalDateTime.now();
		Sale failedSale =
				buildSale(
						slot,
						product,
						userId,
						totalAmount,
						unitPrice,
						PaymentInfo.builder()
								.method(CARD_PAYMENT_METHOD)
								.status(paymentFailureStatus(reason))
								.build(),
						now);
		Sale savedFailure = saleRepository.save(failedSale);

		if (isRetryablePaymentFailure(reason)) {
			return new PaymentAuthorization(
					null,
					PurchaseResponse.builder()
							.saleId(String.valueOf(savedFailure.getId()))
							.status("PENDING_VERIFICATION")
							.statusUrl("/api/sales/" + savedFailure.getId() + "/status")
							.message(pendingVerificationMessage(reason))
							.build());
		}

		throw new PaymentDeclinedException("Payment declined: " + reason);
	}

	private Sale buildSale(
			Slot slot,
			Product product,
			Long userId,
			BigDecimal totalAmount,
			BigDecimal unitPrice,
			PaymentInfo paymentInfo,
			LocalDateTime now) {
		return Sale.builder()
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
	}

	private String paymentFailureStatus(String reason) {
		return isRetryablePaymentFailure(reason)
				? PayStatus.PENDING_VERIFICATION.name()
				: PayStatus.FAILED.name();
	}

	private boolean isRetryablePaymentFailure(String reason) {
		return GATEWAY_TIMEOUT.equals(reason) || NETWORK_ERROR.equals(reason);
	}

	private String pendingVerificationMessage(String reason) {
		return GATEWAY_TIMEOUT.equals(reason)
				? "Payment pending verification"
				: "Retrying payment with same idempotency key";
	}

	private record PaymentAuthorization(PaymentInfo paymentInfo, PurchaseResponse pendingResponse) {}
}
