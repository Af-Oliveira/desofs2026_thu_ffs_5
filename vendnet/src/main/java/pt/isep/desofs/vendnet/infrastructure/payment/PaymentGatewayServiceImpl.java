package pt.isep.desofs.vendnet.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${app.payment.webhook-secret:webhook-secret-placeholder}")
	private String webhookSecret;

	@Override
	public String initiatePayment(String saleId, BigDecimal amount, String currency) {
		log.info("Payment initiated: saleId={}, amount={} {}", saleId, amount, currency);
		return "TXN_" + saleId + "_" + System.currentTimeMillis();
	}

	@Override
	public boolean verifyWebhookSignature(String rawBody, String signatureHeader) {
		try {
			Mac hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec =
					new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			hmac.init(keySpec);

			byte[] computedHash = hmac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
			String computedSignature = HexFormat.of().formatHex(computedHash);

			return MessageDigest.isEqual(
					computedSignature.getBytes(StandardCharsets.UTF_8),
					signatureHeader.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
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
		} catch (Exception e) {
			log.error("Failed to parse webhook body", e);
			return Map.of();
		}
	}
}
