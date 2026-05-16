package pt.isep.desofs.vendnet.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Black-box E2E: registration → login → purchase → customer order history.
 */
@DisplayName("E2E critical flows (HTTP)")
class CriticalFlowsE2ETest extends AbstractHttpBlackBoxE2E {

	@Test
	@DisplayName("Customer: register → login → purchase → GET /api/customer/purchases")
	void register_login_purchase_and_history() {
		String email = "e2e-flow-" + UUID.randomUUID() + "@e2e.vendnet.test";
		String password = "E2eFlow@99";

		given()
				.contentType(ContentType.JSON)
				.body(
						Map.of(
								"email", email,
								"password", password,
								"name", "E2E Flow Customer"))
				.post("/api/auth/register")
				.then()
				.statusCode(200)
				.body("token", not(emptyOrNullString()));

		String token =
				given()
						.contentType(ContentType.JSON)
						.body(Map.of("email", email, "password", password))
						.post("/api/auth/login")
						.then()
						.statusCode(200)
						.extract()
						.path("token");

		long machineId = machineIdForCode(token, "VM-LIS-001");

		Number catalogPriceNum =
				given()
						.header("Authorization", "Bearer " + token)
						.get("/api/products/DRK-001")
						.then()
						.statusCode(200)
						.extract()
						.path("price");
		BigDecimal catalogPrice = new BigDecimal(catalogPriceNum.toString());

		Number salePrice =
				given()
						.header("Authorization", "Bearer " + token)
						.contentType(ContentType.JSON)
						.body(
								Map.of(
										"machineId", machineId,
										"productSku", "DRK-001",
										"quantity", 1))
						.post("/api/customer/purchases")
						.then()
						.statusCode(201)
						.extract()
						.path("price");

		assertThat(new BigDecimal(salePrice.toString())).isEqualByComparingTo(catalogPrice);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> history = given()
				.header("Authorization", "Bearer " + token)
				.get("/api/customer/purchases")
				.then()
				.statusCode(200)
				.extract()
				.jsonPath()
				.getList("$");

		assertThat(history).isNotEmpty();
		assertThat(history.get(0).get("customerEmail")).isEqualTo(email);
	}

	private long machineIdForCode(String bearerToken, String code) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> machines = given()
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
}
