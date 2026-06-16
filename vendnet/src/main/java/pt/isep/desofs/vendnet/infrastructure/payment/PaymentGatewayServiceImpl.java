package pt.isep.desofs.vendnet.infrastructure.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.exception.PaymentGatewayException;

@Slf4j
@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${app.payment.webhook-secret:webhook-secret-placeholder}")
	private String webhookSecret;

	@Override
	public void authorizePayment(String paymentToken, BigDecimal amount) {
		if (paymentToken == null || paymentToken.isBlank()) {
			log.warn("Payment authorization failed: invalid token");
			throw new PaymentGatewayException("Invalid payment token");
		}
		if ("tok_timeout".equals(paymentToken)) {
			throw new PaymentGatewayException("GATEWAY_TIMEOUT");
		}
		if ("tok_network".equals(paymentToken)) {
			throw new PaymentGatewayException("NETWORK_ERROR");
		}
		if ("tok_declined".equals(paymentToken)) {
			throw new PaymentGatewayException("DECLINED");
		}
		log.info("Payment authorized: token={}, amount={} EUR", paymentToken, amount);
	}

	@Override
	public boolean verifyWebhookSignature(String rawBody, String signatureHeader) {
		if (signatureHeader == null || signatureHeader.isBlank()) {
			return false;
		}
		try {
			Mac hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec =
					new SecretKeySpec(
							webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			hmac.init(keySpec);

			byte[] computedHash = hmac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
			String computedSignature = HexFormat.of().formatHex(computedHash);

			return MessageDigest.isEqual(
					computedSignature.getBytes(StandardCharsets.UTF_8),
					signatureHeader.getBytes(StandardCharsets.UTF_8));
		} catch (GeneralSecurityException e) {
			log.error("HMAC verification error", e);
			return false;
		}
	}

	@Override
	public Map<String, String> parseWebhook(String rawBody) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> map = objectMapper.readValue(rawBody, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			log.error("Failed to parse webhook body", e);
			return Map.of();
		}
	}
}
