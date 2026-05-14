package pt.isep.desofs.vendnet.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isep.desofs.vendnet.infrastructure.payment.PaymentGatewayService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/payment")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentGatewayService paymentGatewayService;

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Payment-Signature", required = false) String signature) {

        if (signature == null || !paymentGatewayService.verifyWebhookSignature(rawBody, signature)) {
            log.warn("Invalid payment webhook signature received");
            return ResponseEntity.status(401).body(Map.of("error", "Invalid signature"));
        }

        Map<String, String> data = paymentGatewayService.parseWebhook(rawBody);
        log.info("Payment webhook processed: {}", data);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
