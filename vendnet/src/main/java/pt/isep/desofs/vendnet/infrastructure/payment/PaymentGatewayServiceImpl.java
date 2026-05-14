package pt.isep.desofs.vendnet.infrastructure.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    @Override
    public String initiatePayment(String saleId, BigDecimal amount, String currency) {
        log.info("Payment initiation (saleId={}, amount={} {}) — not yet implemented", saleId, amount, currency);
        return "PENDING_" + saleId;
    }

    @Override
    public boolean verifyWebhookSignature(String rawBody, String signatureHeader) {
        log.info("Webhook signature verification — not yet implemented");
        return false;
    }

    @Override
    public Map<String, String> parseWebhook(String rawBody) {
        log.info("Webhook parsing — not yet implemented");
        return Map.of();
    }
}
