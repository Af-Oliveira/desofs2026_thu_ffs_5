package pt.isep.desofs.vendnet.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * External-actor HTTP tests mapped to Phase&nbsp;1 abuse cases AC-01–AC-08, plus auth bypass, IDOR,
 * and role misuse scenarios requested for black-box coverage.
 */
@DisplayName("Black-box security (AC-01..AC-08 + auth/IDOR)")
class BlackBoxAbuseCasesE2ETest extends AbstractHttpBlackBoxE2E {

	private static final String WEBHOOK_BODY = "{\"saleId\":\"E2E_SALE\",\"status\":\"COMPLETED\"}";

	@Test
	@DisplayName("AC-01: backup without authentication is rejected")
	void ac01_backup_requires_authentication() {
		given().post("/api/admin/operations/backup").then().statusCode(401);
	}

	@Test
	@DisplayName("AC-01: backup as seeded customer is forbidden (not admin)")
	void ac01_backup_rejects_non_admin() {
		String token = login("customer@vendnet.io", "Customer@1234");
		given()
				.header("Authorization", "Bearer " + token)
				.post("/api/admin/operations/backup")
				.then()
				.statusCode(403);
	}

	@Test
	@DisplayName("AC-02: payment webhook without HMAC is rejected")
	void ac02_webhook_missing_signature() {
		given()
				.contentType(ContentType.JSON)
				.body(WEBHOOK_BODY)
				.post("/api/webhooks/payment")
				.then()
				.statusCode(401);
	}

	@Test
	@DisplayName("AC-02: payment webhook with wrong HMAC is rejected")
	void ac02_webhook_wrong_hmac() {
		String sig = hmacSha256(WEBHOOK_BODY, "wrong-secret");
		given()
				.contentType(ContentType.JSON)
				.header("X-Payment-Signature", sig)
				.body(WEBHOOK_BODY)
				.post("/api/webhooks/payment")
				.then()
				.statusCode(401);
	}

	@Test
	@DisplayName("AC-03: purchase payload cannot inject client unitPrice (unknown property)")
	void ac03_rejects_client_supplied_unit_price_field() {
		String token = login("customer@vendnet.io", "Customer@1234");
		String body =
				"{\"machineId\":1,\"productSku\":\"DRK-001\",\"quantity\":1,\"unitPrice\":0.01}";
		given()
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.body(body)
				.post("/api/customer/purchases")
				.then()
				.statusCode(400);
	}

	@Test
	@DisplayName("AC-04: JWT alg=none does not bypass authentication")
	void ac04_alg_none_token_rejected() {
		String header =
				java.util.Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
		String payload =
				java.util.Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"sub\":\"admin@vendnet.io\",\"role\":\"ROLE_ADMINISTRATOR\"}"
										.getBytes(StandardCharsets.UTF_8));
		String token = header + "." + payload + ".";
		given()
				.header("Authorization", "Bearer " + token)
				.get("/api/admin/dashboard")
				.then()
				.statusCode(401);
	}

	@Test
	@DisplayName("AC-05: SQL injection style product id does not return data")
	void ac05_sqli_payload_neutralized() {
		String token = login("customer@vendnet.io", "Customer@1234");
		String sku = encodePathSegment("' OR '1'='1");
		int code =
				given()
						.urlEncodingEnabled(false)
						.header("Authorization", "Bearer " + token)
						.get("/api/products/" + sku)
						.then()
						.extract()
						.statusCode();
		assertThat(code).isIn(400, 404);
	}

	@Test
	@DisplayName("AC-06: path traversal in product path is rejected")
	void ac06_path_traversal_in_product_lookup() {
		String token = login("admin@vendnet.io", "Admin@1234");
		String sku = encodePathSegment("../../etc/passwd");
		int code =
				given()
						.urlEncodingEnabled(false)
						.header("Authorization", "Bearer " + token)
						.get("/api/products/" + sku)
						.then()
						.extract()
						.statusCode();
		assertThat(code).isIn(400, 404);
	}

	@Test
	@DisplayName("AC-07: cannot purchase beyond available slot stock")
	void ac07_oversell_rejected() {
		String email = "e2e-ac07-" + UUID.randomUUID() + "@e2e.vendnet.test";
		String password = "E2eAc07@99";
		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password, "name", "AC07"))
				.post("/api/auth/register")
				.then()
				.statusCode(200);
		String token = login(email, password);
		given()
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.body(Map.of("machineId", 1L, "productSku", "DRK-001", "quantity", 99999))
				.post("/api/customer/purchases")
				.then()
				.statusCode(400);
	}

	@Test
	@DisplayName("AC-08: telemetry endpoint accepts burst without becoming unavailable")
	void ac08_telemetry_ingest_stays_reachable() {
		int successes = 0;
		for (int i = 0; i < 20; i++) {
			int code =
					given()
							.contentType(ContentType.JSON)
							.body(
									"{\"machine\":{\"code\":\"VM-LIS-001\"},\"cpuUsage\":1.0,\"memoryUsage\":1.0,"
											+ "\"diskUsage\":1.0,\"status\":\"ONLINE\",\"uptimeSeconds\":1,"
											+ "\"totalSalesToday\":0,\"temperatureCelsius\":20.0,"
											+ "\"timestamp\":\"2026-05-15T10:00:00\"}")
							.post("/api/telemetry")
							.then()
							.extract()
							.statusCode();
			if (code == 200) {
				successes++;
			}
		}
		assertThat(successes).isPositive();
	}

	@Test
	@DisplayName("Black-box: unauthenticated access to customer purchases is rejected")
	void auth_bypass_customer_history() {
		given().get("/api/customer/purchases").then().statusCode(401);
	}

	@Test
	@DisplayName("Black-box: operator cannot invoke customer purchase API (wrong role)")
	void idor_role_operator_vs_customer_endpoint() {
		String token = login("operator@vendnet.io", "Operator@1234");
		given()
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.body(Map.of("machineId", 1, "productSku", "DRK-001", "quantity", 1))
				.post("/api/customer/purchases")
				.then()
				.statusCode(403);
	}

	@Test
	@DisplayName("Black-box: customer A does not see customer B orders (isolation)")
	void idor_customer_purchase_isolation() {
		String pwd = "E2eIdor@99";
		String emailA = "e2e-idor-a-" + UUID.randomUUID() + "@e2e.vendnet.test";
		String emailB = "e2e-idor-b-" + UUID.randomUUID() + "@e2e.vendnet.test";
		register(emailA, pwd);
		register(emailB, pwd);
		String tokenB = login(emailB, pwd);
		long machineId = machineIdForCode(tokenB, "VM-LIS-001");

		given()
				.header("Authorization", "Bearer " + tokenB)
				.contentType(ContentType.JSON)
				.body(Map.of("machineId", machineId, "productSku", "DRK-002", "quantity", 1))
				.post("/api/customer/purchases")
				.then()
				.statusCode(201);

		String tokenA = login(emailA, pwd);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listA =
				given()
						.header("Authorization", "Bearer " + tokenA)
						.get("/api/customer/purchases")
						.then()
						.statusCode(200)
						.extract()
						.jsonPath()
						.getList("$");
		assertThat(listA).isEmpty();
	}

	private void register(String email, String password) {
		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password, "name", "E2E"))
				.post("/api/auth/register")
				.then()
				.statusCode(200);
	}

	private String login(String email, String password) {
		return given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password))
				.post("/api/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.path("token");
	}

	private long machineIdForCode(String bearerToken, String code) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> machines =
				given()
						.header("Authorization", "Bearer " + bearerToken)
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

	private static String hmacSha256(String data, String key) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/** Path segments must not use {@code +} for space (form encoding); use {@code %20}. */
	private static String encodePathSegment(String raw) {
		return java.net.URLEncoder.encode(raw, StandardCharsets.UTF_8).replace("+", "%20");
	}
}
