package pt.isep.desofs.vendnet.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

/**
 * Black-box security E2E: abuse cases AC-01..AC-08, auth bypass, IDOR, price manipulation.
 *
 * <p>Only HTTP via Rest Assured; profiles {@code e2e} + {@code bootstrap}. Run: {@code ./mvnw
 * verify -Pe2e}
 */
class BlackBoxSecurityE2ETest extends E2ETestBase {

    private static final String WEBHOOK_BODY = "{\"saleId\":\"E2E_SALE\",\"status\":\"COMPLETED\"}";

    /**
     * [AC-01] OS Command Injection (Backup Endpoint)
     *
     * <p>Precondições: tokens admin/operator/customer do bootstrap.
     * Resultado esperado: backup exige ADMIN; outros roles e anónimo rejeitados.
     * Referência: SR-01 / backup sandbox.
     */
    @Test
    void ac01_backupEndpointRequiresAdministratorRole() {
        given().post("/api/admin/backups").then().statusCode(401);

        String customerToken = loginAndGetToken(CUSTOMER_EMAIL, CUSTOMER_PASS);
        given()
                .header("Authorization", authHeader(customerToken))
                .post("/api/admin/backups")
                .then()
                .statusCode(403);

        String operatorToken = loginAndGetToken(OPERATOR_EMAIL, OPERATOR_PASS);
        given()
                .header("Authorization", authHeader(operatorToken))
                .post("/api/admin/backups")
                .then()
                .statusCode(403);

        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASS);
        given()
                .header("Authorization", authHeader(adminToken))
                .post("/api/admin/backups")
                .then()
                .statusCode(201)
                .body("checksum", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()));
    }

    /**
     * [AC-02] Forged Payment Webhook (HMAC Bypass)
     *
     * <p>Precondições: secret {@code e2e-webhook-secret} no perfil e2e.
     * Resultado esperado: assinatura inválida → 401; HMAC correcto → 200 ok.
     * Referência: SR-02 / M-02.
     */
    @Test
    void ac02_paymentWebhookHmacValidation() {
        given()
                .contentType(ContentType.JSON)
                .body(WEBHOOK_BODY)
                .post("/api/webhooks/payment")
                .then()
                .statusCode(401)
                .body("error", org.hamcrest.Matchers.equalTo("Invalid signature"));

        String wrongSig = computeHmacSha256(WEBHOOK_BODY, "wrong-secret");
        given()
                .contentType(ContentType.JSON)
                .header("X-Payment-Signature", wrongSig)
                .body(WEBHOOK_BODY)
                .post("/api/webhooks/payment")
                .then()
                .statusCode(401)
                .body("error", org.hamcrest.Matchers.equalTo("Invalid signature"));

        String validSig = computeHmacSha256(WEBHOOK_BODY, WEBHOOK_SECRET);
        String tamperedBody = "{\"saleId\":\"E2E_SALE\",\"status\":\"TAMPERED\"}";
        given()
                .contentType(ContentType.JSON)
                .header("X-Payment-Signature", validSig)
                .body(tamperedBody)
                .post("/api/webhooks/payment")
                .then()
                .statusCode(401)
                .body("error", org.hamcrest.Matchers.equalTo("Invalid signature"));

        given()
                .contentType(ContentType.JSON)
                .header("X-Payment-Signature", validSig)
                .body(WEBHOOK_BODY)
                .post("/api/webhooks/payment")
                .then()
                .statusCode(200)
                .body("status", org.hamcrest.Matchers.equalTo("ok"));
    }

    /**
     * [AC-03] Client-Supplied Price Manipulation
     *
     * <p>Precondições: customer JWT; DRK-002 (água €1.00) em VM-LIS-001.
     * Resultado esperado: campos extra de preço rejeitados com 400 antes do processamento.
     * Referência: SR-24 / SR-29 — {@code PurchaseRequest} sem unitPrice/price e Jackson
     * strict-deserialization activo.
     */
    @Test
    void ac03_clientCannotOverrideCatalogPrice() {
        String token = loginAndGetToken(CUSTOMER_EMAIL, CUSTOMER_PASS);
        Long productId = getProductIdBySku("DRK-002", token);
        Long machineId = getMachineIdByCode("VM-LIS-001", token);
        given()
                .header("Authorization", authHeader(token))
                .contentType(ContentType.JSON)
                .body(
                        "{\"productId\":"
                                + productId
                                + ",\"machineId\":"
                                + machineId
                                + ",\"paymentToken\":\"tok_ac03\","
                                + "\"idempotencyKey\":\"ac03-a-"
                                + UUID.randomUUID()
                                + "\",\"unitPrice\":0.01}")
                .post("/api/sales/purchase")
                .then()
                .statusCode(400)
                .body("message", org.hamcrest.Matchers.equalTo("Invalid request body"));

        given()
                .header("Authorization", authHeader(token))
                .contentType(ContentType.JSON)
                .body(
                        "{\"productId\":"
                                + productId
                                + ",\"machineId\":"
                                + machineId
                                + ",\"paymentToken\":\"tok_ac03b\","
                                + "\"idempotencyKey\":\"ac03-b-"
                                + UUID.randomUUID()
                                + "\",\"price\":-1}")
                .post("/api/sales/purchase")
                .then()
                .statusCode(400)
                .body("message", org.hamcrest.Matchers.equalTo("Invalid request body"));

        String validSaleId =
                given()
                        .header("Authorization", authHeader(token))
                        .contentType(ContentType.JSON)
                        .body(
                                Map.of(
                                        "productId",
                                        productId,
                                        "machineId",
                                        machineId,
                                        "paymentToken",
                                        "tok_ac03_valid",
                                        "idempotencyKey",
                                        "ac03-c-" + UUID.randomUUID()))
                        .post("/api/sales/purchase")
                        .then()
                        .statusCode(201)
                        .body("status", org.hamcrest.Matchers.equalTo("COMPLETED"))
                        .extract()
                        .path("saleId");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sales =
                given()
                        .header("Authorization", authHeader(token))
                        .get("/api/sales/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$");

        Map<String, Object> sale =
                sales.stream()
                        .filter(s -> validSaleId.equals(String.valueOf(s.get("id"))))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Sale " + validSaleId + " not found"));

        BigDecimal unitPrice = new BigDecimal(sale.get("unitPrice").toString());
        assertThat(unitPrice).isEqualByComparingTo(new BigDecimal("1.00"));
    }

    /**
     * [AC-04] JWT alg:none Signature Bypass
     *
     * <p>Precondições: nenhuma.
     * Resultado esperado: tokens forjados rejeitados com 401.
     * Referência: SR-03 / JwtService HS256.
     */
    @Test
    void ac04_jwtAlgNoneAndInvalidTokensRejected() {
        String adminAlgNone = buildAlgNoneToken("admin@vendnet.io", "ROLE_ADMINISTRATOR");
        given()
                .header("Authorization", authHeader(adminAlgNone))
                .get("/api/admin/dashboard")
                .then()
                .statusCode(401);

        String customerAlgNone = buildAlgNoneToken("customer@vendnet.io", "ROLE_CUSTOMER");
        given()
                .header("Authorization", authHeader(customerAlgNone))
                .get("/api/auth/me")
                .then()
                .statusCode(401);

        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                "{\"alg\":\"none\",\"typ\":\"JWT\"}"
                                        .getBytes(StandardCharsets.UTF_8));
        String payload =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                "{\"sub\":\"admin@vendnet.io\",\"role\":\"ROLE_ADMINISTRATOR\",\"exp\":9999999999}"
                                        .getBytes(StandardCharsets.UTF_8));
        given()
                .header("Authorization", "Bearer " + header + "." + payload + ".")
                .get("/api/admin/users")
                .then()
                .statusCode(401);

        given()
                .header("Authorization", "Bearer not-a-valid-jwt-token")
                .get("/api/auth/me")
                .then()
                .statusCode(401);
    }

    /**
     * [AC-05] SQL Injection
     *
     * <p>Precondições: customer JWT.
     * Resultado esperado: payloads injectados → 400/404; sem stack trace no body.
     * Referência: SR-05.
     */
    @Test
    void ac05_sqlInjectionPayloadsNeutralized() {
        String token = loginAndGetToken(CUSTOMER_EMAIL, CUSTOMER_PASS);

        String skuPayload = encodePathSegment("' OR '1'='1");
        String bodyProducts =
                given()
                        .urlEncodingEnabled(false)
                        .header("Authorization", authHeader(token))
                        .get("/api/products/" + skuPayload)
                        .then()
                        .extract()
                        .asString();
	        int codeProducts =
	                given()
	                        .urlEncodingEnabled(false)
	                        .header("Authorization", authHeader(token))
	                        .get("/api/products/" + skuPayload)
                        .then()
	                        .extract()
	                        .statusCode();
	        assertThat(codeProducts).isIn(400, 404);
	        assertThat(bodyProducts).doesNotContain("java.lang.", "SQLException");

        String idPayload = encodePathSegment("1 UNION SELECT * FROM users--");
        int codeId =
                given()
                        .urlEncodingEnabled(false)
                        .header("Authorization", authHeader(token))
                        .get("/api/products/" + idPayload)
                        .then()
                        .extract()
                        .statusCode();
        assertThat(codeId).isIn(400, 404);
    }

    /**
     * [AC-06] Path Traversal
     *
     * <p>Precondições: admin JWT.
     * Resultado esperado: paths de relatório dentro do sandbox; traversal em produtos → 400/404.
     * Referência: PathValidatorImpl / SR-06.
     */
    @Test
    void ac06_pathTraversalPrevented() {
        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASS);

        var reportResponse =
                given()
                        .header("Authorization", authHeader(adminToken))
                        .contentType(ContentType.JSON)
                        .body("{\"reportType\":\"../../etc/passwd\"}")
                        .post("/api/admin/operations/reports/sales")
                        .then()
                        .extract();
        int reportStatus = reportResponse.statusCode();
        assertThat(reportStatus).isIn(200, 500);
	        if (reportStatus == 200) {
	            String reportPath = reportResponse.path("path");
	            assertThat(reportPath.replace('\\', '/')).doesNotContain("..").contains("/var/vendnet/");
	        }

        String traversalSku = encodePathSegment("../../etc/passwd");
        int code =
                given()
                        .urlEncodingEnabled(false)
                        .header("Authorization", authHeader(adminToken))
                        .get("/api/products/" + traversalSku)
                        .then()
                        .extract()
                        .statusCode();
        assertThat(code).isIn(400, 404);
    }

    /**
     * [AC-07] TOCTOU Race Condition (Concurrent Purchases)
     *
     * <p>Precondições: customer único; stock de DRK-003 em VM-LIS-001 reduzido a 1 unidade.
     * Resultado esperado: apenas 1 COMPLETED; restantes 409/422/500; vendas ≤ stock.
     * Referência: SR-15 — {@code Slot.@Version}; {@code SaleService} @Transactional.
     */
    @Test
    void ac07_concurrentPurchasesRespectStock() throws Exception {
        String email = "e2e-ac07-" + UUID.randomUUID() + "@vendnet.io";
        String password = "E2eAc07@99";
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password, "name", "AC07 Race"))
                .post("/api/auth/register")
                .then()
                .statusCode(200);

        String token = loginAndGetToken(email, password);
        Long productId = getProductIdBySku("DRK-003", token);
        Long machineId = getMachineIdByCode("VM-LIS-001", token);

        given()
                .header("Authorization", authHeader(token))
                .get("/api/machines")
                .then()
                .statusCode(200);

        for (int i = 0; i < 14; i++) {
            int code =
                    given()
                            .header("Authorization", authHeader(token))
                            .contentType(ContentType.JSON)
                            .body(
                                    Map.of(
                                            "productId",
                                            productId,
                                            "machineId",
                                            machineId,
                                            "paymentToken",
                                            "tok_deplete_" + i,
                                            "idempotencyKey",
                                            "deplete-" + UUID.randomUUID()))
                            .post("/api/sales/purchase")
                            .then()
                            .extract()
                            .statusCode();
            if (code != 201) {
                break;
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            tasks.add(
                    () ->
                            given()
                                    .header("Authorization", authHeader(token))
                                    .contentType(ContentType.JSON)
                                    .body(
                                            Map.of(
                                                    "productId",
                                                    productId,
                                                    "machineId",
                                                    machineId,
                                                    "paymentToken",
                                                    "tok_race_" + idx,
                                                    "idempotencyKey",
                                                    "race-" + UUID.randomUUID()))
                                    .post("/api/sales/purchase")
                                    .then()
                                    .extract()
                                    .statusCode());
        }

        int completed = 0;
        int rejected = 0;
        try {
            for (Future<Integer> f : pool.invokeAll(tasks)) {
                int status = f.get();
                if (status == 201) {
                    completed++;
                } else if (status == 409 || status == 422 || status == 500) {
                    rejected++;
                }
            }
        } finally {
            pool.shutdown();
        }

        assertThat(completed).isEqualTo(1);
        assertThat(rejected).isGreaterThanOrEqualTo(1);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mySales =
                given()
                        .header("Authorization", authHeader(token))
                        .get("/api/sales/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$");

        long juiceSales =
                mySales.stream()
                        .filter(
                                s -> {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> p = (Map<String, Object>) s.get("product");
                                    return "DRK-003".equals(p.get("sku"));
                                })
                        .count();
        assertThat(juiceSales).isLessThanOrEqualTo(15);
    }

    /**
     * [AC-08] Telemetry DoS / Rate Limiting
     *
     * <p>Precondições: endpoint permitAll.
     * Resultado esperado: payload válido aceite; inválido 4xx; burst sem 500.
     * Referência: SR-08 — validação estrutural; rate limit depende de infra.
     */
    @Test
    void ac08_telemetryEndpointValidationAndBurst() {
        String timestamp = java.time.LocalDateTime.now().minusSeconds(5).toString();
        String validPayload =
                "{\"serialNumber\":\"VM-LIS-001\",\"temperature\":22.5,"
                        + "\"stockLevels\":{\"A1\":10,\"A2\":12,\"B1\":8},\"statusCode\":\"ONLINE\",\"errorCodes\":[],"
                        + "\"timestamp\":\"" + timestamp + "\"}";

        given()
                .contentType(ContentType.JSON)
                .header("X-Machine-CN", "VM-LIS-001")
                .body(validPayload)
                .post("/api/machines/telemetry")
                .then()
                .statusCode(200)
                .body("accepted", org.hamcrest.Matchers.equalTo(true));

        given().contentType(ContentType.JSON).post("/api/machines/telemetry").then().statusCode(400);

        given()
                .contentType(ContentType.JSON)
                .header("X-Machine-CN", "VM-NO-SUCH")
                .body(
                        "{\"serialNumber\":\"VM-NO-SUCH\",\"temperature\":20.0,"
                                + "\"stockLevels\":{\"A1\":10},\"statusCode\":\"ONLINE\",\"errorCodes\":[],"
                                + "\"timestamp\":\"" + timestamp + "\"}")
                .post("/api/machines/telemetry")
                .then()
                .statusCode(403);

        int successes = 0;
        int rateLimited = 0;
        for (int i = 0; i < 5; i++) {
            int code =
                    given()
                            .contentType(ContentType.JSON)
                            .header("X-Machine-CN", "VM-LIS-001")
                            .body(validPayload)
                            .post("/api/machines/telemetry")
                            .then()
                            .extract()
                            .statusCode();
            if (code >= 200 && code < 300) {
                successes++;
            }
            if (code == 429) {
                rateLimited++;
            }
            assertThat(code).isNotEqualTo(500);
        }
        assertThat(successes).isPositive();
        assertThat(rateLimited).isPositive();
    }

    /**
     * IDOR: Customer A não vê vendas do Customer B
     *
     * <p>Precondições: dois customers registados; B com compra.
     * Resultado esperado: GET /api/sales/me de A não inclui vendas de B.
     * Referência: isolamento por userId em SaleService.
     */
    @Test
    void idor_customerCannotSeeOtherUsersSales() {
        String pwd = "E2eIdor@99";
        String emailA = "e2e-idor-a-" + UUID.randomUUID() + "@vendnet.io";
        String emailB = "e2e-idor-b-" + UUID.randomUUID() + "@vendnet.io";

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", emailA, "password", pwd, "name", "Idor A"))
                .post("/api/auth/register")
                .then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", emailB, "password", pwd, "name", "Idor B"))
                .post("/api/auth/register")
                .then()
                .statusCode(200);

        String tokenB = loginAndGetToken(emailB, pwd);
        Long machineId = getMachineIdByCode("VM-LIS-001", tokenB);
        Long productId = getProductIdBySku("DRK-002", tokenB);

        given()
                .header("Authorization", authHeader(tokenB))
                .contentType(ContentType.JSON)
                .body(
                        Map.of(
                                "productId",
                                productId,
                                "machineId",
                                machineId,
                                "paymentToken",
                                "tok_idor_b",
                                "idempotencyKey",
                                "idor-b-" + UUID.randomUUID()))
                .post("/api/sales/purchase")
                .then()
                .statusCode(201);

        String tokenA = loginAndGetToken(emailA, pwd);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listA =
                given()
                        .header("Authorization", authHeader(tokenA))
                        .get("/api/sales/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$");

        assertThat(listA).isEmpty();
    }

    /**
     * IDOR: Customer não acede a vendas por máquina (endpoint de operador)
     *
     * <p>Precondições: customer JWT; máquina VM-LIS-001.
     * Resultado esperado: GET /api/sales/machine/{id} → 403.
     * Referência: RBAC SaleController.
     */
    @Test
    void idor_customerCannotAccessSalesByMachine_returns403() {
        String token = loginAndGetToken(CUSTOMER_EMAIL, CUSTOMER_PASS);
        Long machineId = getMachineIdByCode("VM-LIS-001", token);

        given()
                .header("Authorization", authHeader(token))
                .get("/api/sales/machine/" + machineId)
                .then()
                .statusCode(403);
    }

    private static String buildAlgNoneToken(String subject, String role) {
        String header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                "{\"alg\":\"none\",\"typ\":\"JWT\"}"
                                        .getBytes(StandardCharsets.UTF_8));
        String payload =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                ("{\"sub\":\""
                                        + subject
                                        + "\",\"role\":\""
                                        + role
                                        + "\",\"exp\":9999999999}")
                                        .getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }
}
