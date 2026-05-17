package pt.isep.desofs.vendnet.infrastructure.payment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PaymentGatewayServiceImplTest {

	private PaymentGatewayServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new PaymentGatewayServiceImpl();
		ReflectionTestUtils.setField(service, "webhookSecret", "test-secret-key");
	}

	@Test
	void authorizePayment_validToken_shouldNotThrow() {
		assertDoesNotThrow(() -> service.authorizePayment("tok_12345", new java.math.BigDecimal("10.00")));
	}

	@Test
	void authorizePayment_nullToken_shouldThrow() {
		assertThrows(RuntimeException.class, () -> service.authorizePayment(null, new java.math.BigDecimal("10.00")));
	}

	@Test
	void authorizePayment_blankToken_shouldThrow() {
		assertThrows(RuntimeException.class, () -> service.authorizePayment("   ", new java.math.BigDecimal("10.00")));
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}