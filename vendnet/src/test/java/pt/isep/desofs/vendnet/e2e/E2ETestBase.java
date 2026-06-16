package pt.isep.desofs.vendnet.e2e;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base for VendNet HTTP black-box E2E tests.
 *
 * <p>Profiles: {@code e2e} (H2 in-memory) + {@code bootstrap} ({@link
 * pt.isep.desofs.vendnet.application.service.BootstrapService#seed()}).
 *
 * <p>Run: {@code ./mvnw verify -Pe2e} or {@code ./mvnw failsafe:integration-test -Pe2e}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"e2e", "bootstrap"})
public abstract class E2ETestBase {

    protected static final String ADMIN_EMAIL = "admin@vendnet.io";
    protected static final String ADMIN_PASS = "Admin@123456";
    protected static final String OPERATOR_EMAIL = "operator@vendnet.io";
    protected static final String OPERATOR_PASS = "Operator@123456";
    protected static final String CUSTOMER_EMAIL = "customer@vendnet.io";
    protected static final String CUSTOMER_PASS = "Customer@123456";
    protected static final String WEBHOOK_SECRET = "e2e-webhook-secret";

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected String loginAndGetToken(String usernameOrEmail, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", usernameFromEmail(usernameOrEmail), "password", password))
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    protected String usernameFromEmail(String usernameOrEmail) {
        String username =
                usernameOrEmail.contains("@")
                        ? usernameOrEmail.substring(0, usernameOrEmail.indexOf('@'))
                        : usernameOrEmail;
        String sanitized = username.replaceAll("[^A-Za-z0-9]", "");
        return sanitized.length() <= 30 ? sanitized : sanitized.substring(0, 30);
    }

    protected String authHeader(String token) {
        return "Bearer " + token;
    }

    protected String computeHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Long getMachineIdByCode(String code, String token) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> machines =
                given()
                        .header("Authorization", authHeader(token))
                        .get("/api/machines")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$");

        return machines.stream()
                .filter(m -> code.equals(m.get("code")))
                .map(m -> ((Number) m.get("id")).longValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No machine with code " + code));
    }

    protected Long getProductIdBySku(String sku, String token) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products =
                given()
                        .header("Authorization", authHeader(token))
                        .get("/api/products")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$");

        return products.stream()
                .filter(p -> sku.equals(p.get("sku")))
                .map(p -> ((Number) p.get("id")).longValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No product with sku " + sku));
    }

    protected static String encodePathSegment(String raw) {
        return java.net.URLEncoder.encode(raw, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
