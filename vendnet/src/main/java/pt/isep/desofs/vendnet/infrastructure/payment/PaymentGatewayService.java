package pt.isep.desofs.vendnet.infrastructure.payment;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {

    String initiatePayment(String saleId, BigDecimal amount, String currency);

    boolean verifyWebhookSignature(String rawBody, String signatureHeader);

    Map<String, String> parseWebhook(String rawBody);
}
