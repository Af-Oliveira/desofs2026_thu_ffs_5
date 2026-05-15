package pt.isep.desofs.vendnet.infrastructure.payment;

import java.math.BigDecimal;

public interface PaymentGatewayService {

	void authorizePayment(String paymentToken, BigDecimal amount);

	boolean verifyWebhookSignature(String rawBody, String signatureHeader);

	java.util.Map<String, String> parseWebhook(String rawBody);
}
