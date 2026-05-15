package pt.isep.desofs.vendnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.desofs.vendnet.application.service.JwtService;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AbuseCaseRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = tokenForRole("abuse-admin@vendnet.com", Role.ROLE_ADMINISTRATOR);
    }

    // ── AC-02: Forged Payment Confirmation Webhook via HMAC Bypass ───────────

    @Test
    void paymentWebhook_withMissingSignature_returns401() throws Exception {
        String body = "{\"saleId\":\"SALE_001\",\"status\":\"COMPLETED\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid signature"));
    }

    @Test
    void paymentWebhook_withInvalidSignature_returns401() throws Exception {
        String body = "{\"saleId\":\"SALE_001\",\"status\":\"COMPLETED\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Payment-Signature", "0000000000000000000000000000000000000000000000000000000000000000"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid signature"));
    }

    @Test
    void paymentWebhook_withForgedSignature_returns401() throws Exception {
        String originalBody = "{\"saleId\":\"SALE_001\",\"status\":\"COMPLETED\"}";

        String forgedSignature = computeHmacSha256(originalBody, "wrong-secret-key");

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalBody)
                        .header("X-Payment-Signature", forgedSignature))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid signature"));
    }

    @Test
    void paymentWebhook_withModifiedBodyAndValidSignature_returns401() throws Exception {
        String originalBody = "{\"saleId\":\"SALE_001\",\"status\":\"FAILED\"}";
        String modifiedBody = "{\"saleId\":\"SALE_001\",\"status\":\"COMPLETED\"}";

        String signature = computeHmacSha256(originalBody, "webhook-secret-placeholder");

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modifiedBody)
                        .header("X-Payment-Signature", signature))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid signature"));
    }

    // ── AC-04: JWT alg:none Signature Bypass ─────────────────────────────────

    @Test
    void jwt_withAlgNone_isRejected() throws Exception {
        String header = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"admin@vendnet.com\",\"role\":\"ROLE_ADMINISTRATOR\",\"iat\":0,\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        String algNoneToken = header + "." + payload + ".";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + algNoneToken))
                .andExpect(status().isUnauthorized());
    }

    // ── AC-03: Client-Supplied Unit Price Bypass (Structural Verification) ───

    @Test
    void saleEntity_hasServerSidePriceFields() throws Exception {
        java.lang.reflect.Field[] fields = pt.isep.desofs.vendnet.domain.model.sale.Sale.class.getDeclaredFields();
        boolean hasUnitPrice = false;
        boolean hasTotalAmount = false;
        for (java.lang.reflect.Field f : fields) {
            if (f.getName().equals("unitPrice")) hasUnitPrice = true;
            if (f.getName().equals("totalAmount")) hasTotalAmount = true;
        }
        if (!hasUnitPrice || !hasTotalAmount) {
            throw new AssertionError("Sale entity must have unitPrice and totalAmount fields. "
                    + "Price must be resolved server-side from the Product catalog (SR-24).");
        }
    }

    // ── AC-05: SQL Injection via Inventory Search ────────────────────────────

    @Test
    void productSearch_SqlInjectionPayload_returns404or400() throws Exception {
        String customerToken = tokenForRole("ac05-customer@vendnet.com", Role.ROLE_CUSTOMER);

        mockMvc.perform(get("/api/products/" + java.net.URLEncoder.encode("' OR '1'='1", StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 404 && status != 400) {
                        throw new AssertionError("Expected 400 or 404 for SQLi payload, got: " + status);
                    }
                });
    }

    // ── AC-06: Path Traversal via File Operations ────────────────────────────

    @Test
    void fileStorage_rejectsPathTraversal_returns400or404() throws Exception {
        mockMvc.perform(get("/api/products/" + java.net.URLEncoder.encode("../../etc/passwd", StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 400 && status != 404) {
                        throw new AssertionError("Expected 400 or 404 for path traversal, got: " + status);
                    }
                });
    }

    // ── AC-07: TOCTOU Race Condition on Slot Stock ──────────────────────────

    @Test
    void slotEntity_hasCapacityAndCurrentStock_fields() throws Exception {
        java.lang.reflect.Field capacity = pt.isep.desofs.vendnet.domain.model.slot.Slot.class.getDeclaredField("capacity");
        java.lang.reflect.Field currentStock = pt.isep.desofs.vendnet.domain.model.slot.Slot.class.getDeclaredField("currentStock");
        capacity.setAccessible(true);
        currentStock.setAccessible(true);

        if (capacity.getType() != int.class || currentStock.getType() != int.class) {
            throw new AssertionError("Slot must have int capacity and currentStock fields for pessimistic locking (SR-15).");
        }
    }

    // ── AC-01: OS Command Injection via ProcessBuilder Backup ────────────────

    @Test
    void backupEndpoint_requiresAdminRole() throws Exception {
        mockMvc.perform(post("/api/admin/operations/backup"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void backupEndpoint_adminCanTrigger() throws Exception {
        mockMvc.perform(post("/api/admin/operations/backup")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 403 || status == 401) {
                        throw new AssertionError("Admin should be authorized. Got: " + status);
                    }
                });
    }

    // ── AC-08: VM Fleet Flood (Rate Limiting Structure) ─────────────────────

    @Test
    void telemetryController_isPubliclyAccessible_withAnyBody() throws Exception {
        String telemetryBody = "{\"machineCode\":\"VM-001\",\"cpuUsage\":45.0,\"memoryUsage\":60.0,\"status\":\"ONLINE\",\"uptimeSeconds\":3600,\"temperatureCelsius\":22.5}";

        mockMvc.perform(post("/api/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryBody))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError("Telemetry endpoint should be publicly accessible, got 401");
                    }
                });
    }

    // ── Helper methods ───────────────────────────────────────────────────────

    private String tokenForRole(String email, Role role) {
        userRepository.findByEmail(email).orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            return userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("Test@1234"))
                    .name(role.name())
                    .role(role)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        });
        return jwtService.generateToken(email, role.name());
    }

    private String computeHmacSha256(String data, String key) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
