package pt.isep.desofs.vendnet.domain.model.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PaymentInfoTest {

	@Test
	void builder_shouldCreatePaymentInfoWithAllFields() {
		PaymentInfo info = PaymentInfo.builder()
				.method("CREDIT_CARD")
				.transactionRef("TXN-123456")
				.status("COMPLETED")
				.build();

		assertEquals("CREDIT_CARD", info.getMethod());
		assertEquals("TXN-123456", info.getTransactionRef());
		assertEquals("COMPLETED", info.getStatus());
	}

	@Test
	void builder_shouldAllowPartialPaymentInfo() {
		PaymentInfo info = PaymentInfo.builder().method("CREDIT_CARD").build();

		assertEquals("CREDIT_CARD", info.getMethod());
		assertNull(info.getTransactionRef());
		assertNull(info.getStatus());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyPaymentInfo() {
		PaymentInfo info = new PaymentInfo();

		assertNull(info.getMethod());
		assertNull(info.getTransactionRef());
		assertNull(info.getStatus());
	}

	@Test
	void allArgsConstructor_shouldCreatePaymentInfo() {
		PaymentInfo info = new PaymentInfo("DEBIT_CARD", "TXN-789", "PENDING");

		assertEquals("DEBIT_CARD", info.getMethod());
		assertEquals("TXN-789", info.getTransactionRef());
		assertEquals("PENDING", info.getStatus());
	}

	@Test
	void paymentInfo_shouldHandleNullFields() {
		PaymentInfo info = PaymentInfo.builder().build();

		assertNull(info.getMethod());
		assertNull(info.getTransactionRef());
		assertNull(info.getStatus());
	}
}
