package pt.isep.desofs.vendnet.infrastructure.payment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import pt.isep.desofs.vendnet.domain.exception.PaymentGatewayException;

class PaymentGatewayServiceImplTest {

	private PaymentGatewayServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PaymentGatewayServiceImpl();
		ReflectionTestUtils.setField(service, "webhookSecret", "test-secret-key");
	}

	@Test
	void authorizePayment_validToken_shouldNotThrow() {
		BigDecimal amount = new BigDecimal("10.00");
		assertDoesNotThrow(() -> service.authorizePayment("tok_12345", amount));
	}

	@Test
	void authorizePayment_nullToken_shouldThrow() {
		BigDecimal amount = new BigDecimal("10.00");
		assertThrows(PaymentGatewayException.class, () -> service.authorizePayment(null, amount));
	}

	@Test
	void authorizePayment_blankToken_shouldThrow() {
		BigDecimal amount = new BigDecimal("10.00");
		assertThrows(PaymentGatewayException.class, () -> service.authorizePayment("   ", amount));
	}

	@Test
	void verifyWebhookSignature_valid_shouldReturnTrue() {
		String body = "{\"event\":\"payment.completed\"}";
		String signature = computeHmac(body, "test-secret-key");
		assertTrue(service.verifyWebhookSignature(body, signature));
	}

	@Test
	void verifyWebhookSignature_invalid_shouldReturnFalse() {
		String body = "{\"event\":\"payment.completed\"}";
		assertFalse(service.verifyWebhookSignature(body, "wrong-signature"));
	}

	@Test
	void verifyWebhookSignature_nullSignature_shouldReturnFalse() {
		assertFalse(service.verifyWebhookSignature("body", null));
	}

	@Test
	void parseWebhook_validJson_shouldReturnMap() {
		String body = "{\"event\":\"payment.completed\",\"status\":\"success\"}";
		Map<String, String> result = service.parseWebhook(body);
		assertEquals("payment.completed", result.get("event"));
		assertEquals("success", result.get("status"));
	}

	@Test
	void parseWebhook_invalidJson_shouldReturnEmptyMap() {
		Map<String, String> result = service.parseWebhook("not json");
		assertTrue(result.isEmpty());
	}

	private String computeHmac(String data, String key) {
		try {
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
			javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA256");
			mac.init(keySpec);
			byte[] hash = mac.doFinal(data.getBytes());
			return java.util.HexFormat.of().formatHex(hash);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}
}
