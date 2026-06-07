package pt.isep.desofs.vendnet.e2e;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Critical business flows exercised end-to-end over HTTP (Rest Assured).
 *
 * <p>Run: {@code ./mvnw verify -Pe2e}
 */
class CriticalFlowE2ETest extends E2ETestBase {

	/**
	 * [E2E-01] Registo → Login → Compra → Histórico
	 *
	 * <p>Precondições: perfis {@code e2e} + {@code bootstrap}; máquinas e produtos semeados.
	 * Dados de seed: VM-LIS-001, DRK-001/DRK-002.
	 * Resultado esperado: compra COMPLETED e venda visível em GET /api/sales/me.
	 * Referência: fluxo cliente FR de vendas.
	 */
	@Test
	void e2e01_registerLoginPurchaseAndHistory() {
		String email = "e2e-reg-" + UUID.randomUUID() + "@vendnet.io";
		String password = "E2eFlow@99";
		String name = "E2E Flow Customer";

		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password, "name", name))
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
						.body("token", not(emptyOrNullString()))
						.body("email", org.hamcrest.Matchers.equalTo(email))
						.extract()
						.path("token");

		given()
				.header("Authorization", authHeader(token))
				.get("/api/products")
				.then()
				.statusCode(200)
				.body("$", not(emptyOrNullString()));

		Long productId;
		try {
			productId = getProductIdBySku("DRK-001", token);
		} catch (AssertionError ignored) {
			productId = getProductIdBySku("DRK-002", token);
		}
		Long machineId = getMachineIdByCode("VM-LIS-001", token);
		String idempotencyKey = "e2e-01-" + UUID.randomUUID();

		String saleId =
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
										"tok_e2e_test",
										"idempotencyKey",
										idempotencyKey))
						.post("/api/sales/purchase")
						.then()
						.statusCode(201)
						.body("status", org.hamcrest.Matchers.equalTo("COMPLETED"))
						.body("message", not(emptyOrNullString()))
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
						.filter(s -> saleId.equals(String.valueOf(s.get("id"))))
						.findFirst()
						.orElseThrow(() -> new AssertionError("Sale " + saleId + " not in history"));

		assertThat(sale.get("saleDate")).isNotNull();
		@SuppressWarnings("unchecked")
		Map<String, Object> product = (Map<String, Object>) sale.get("product");
		@SuppressWarnings("unchecked")
		Map<String, Object> machine = (Map<String, Object>) sale.get("machine");
		assertThat(((Number) product.get("id")).longValue()).isEqualTo(productId);
		assertThat(((Number) machine.get("id")).longValue()).isEqualTo(machineId);
	}

	/**
	 * [E2E-02] Três logins falhados não bloqueiam a conta
	 *
	 * <p>Precondições: utilizador registado com email único.
	 * Resultado esperado: 3×401; 4.º login com password correcta → 200.
	 * Referência: FR-07 lockout (limiar &gt; 3).
	 */
	@Test
	void e2e02_threeFailedLogins_accountStillUnlocked() {
		String email = "e2e-lock3-" + UUID.randomUUID() + "@vendnet.io";
		String password = "E2eLock@99";

		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password, "name", "Lock3 Test"))
				.post("/api/auth/register")
				.then()
				.statusCode(200);

		for (int i = 0; i < 3; i++) {
			given()
					.contentType(ContentType.JSON)
					.body(Map.of("email", email, "password", "WrongPass!"))
					.post("/api/auth/login")
					.then()
					.statusCode(401);
		}

		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password))
				.post("/api/auth/login")
				.then()
				.statusCode(200)
				.body("token", not(emptyOrNullString()));
	}

	/**
	 * [E2E-03] Lockout após 5 falhas
	 *
	 * <p>Precondições: utilizador registado.
	 * Resultado esperado: 5×401; 6.º login correcto → 401 {@code Account Locked}.
	 * Referência: FR-07 / SR account lockout.
	 */
	@Test
	void e2e03_fiveFailedLogins_accountLocked() {
		String email = "e2e-lock5-" + UUID.randomUUID() + "@vendnet.io";
		String password = "E2eLock5@99";

		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password, "name", "Lock5 Test"))
				.post("/api/auth/register")
				.then()
				.statusCode(200);

		for (int i = 0; i < 5; i++) {
			given()
					.contentType(ContentType.JSON)
					.body(Map.of("email", email, "password", "WrongPass!"))
					.post("/api/auth/login")
					.then()
					.statusCode(401);
		}

		given()
				.contentType(ContentType.JSON)
				.body(Map.of("email", email, "password", password))
				.post("/api/auth/login")
				.then()
				.statusCode(401)
				.body("error", org.hamcrest.Matchers.equalTo("Account Locked"));
	}

	/**
	 * [E2E-04] Operador consulta vendas por máquina
	 *
	 * <p>Precondições: seed com vendas em VM-LIS-001.
	 * Resultado esperado: GET /api/sales/machine/{id} → 200, lista não vazia.
	 * Referência: RBAC operador.
	 */
	@Test
	void e2e04_operatorViewsMachineSales() {
		String token = loginAndGetToken(OPERATOR_EMAIL, OPERATOR_PASS);
		Long machineId = getMachineIdByCode("VM-LIS-001", token);

		given()
				.header("Authorization", authHeader(token))
				.get("/api/sales/machine/" + machineId)
				.then()
				.statusCode(200)
				.body("$", not(emptyOrNullString()));
	}

	/**
	 * [E2E-05] Administrador: dashboard e utilizadores
	 *
	 * <p>Precondições: seed admin, operator, customer.
	 * Resultado esperado: dashboard com message; ≥3 utilizadores.
	 * Referência: RBAC administrador.
	 */
	@Test
	void e2e05_adminDashboardAndUsers() {
		String token = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASS);

		given()
				.header("Authorization", authHeader(token))
				.get("/api/admin/dashboard")
				.then()
				.statusCode(200)
				.body("message", not(emptyOrNullString()));

		given()
				.header("Authorization", authHeader(token))
				.get("/api/admin/users")
				.then()
				.statusCode(200)
				.body("$", hasSize(greaterThanOrEqualTo(3)));
	}

	/**
	 * [E2E-06] Idempotência de compra
	 *
	 * <p>Precondições: customer@vendnet.io semeado; stock disponível.
	 * Resultado esperado: 1.ª compra COMPLETED; 2.ª DUPLICATE com mesmo saleId.
	 * Referência: idempotency key M-03.
	 */
	@Test
	void e2e06_purchaseIdempotencyDuplicate() {
		String token = loginAndGetToken(CUSTOMER_EMAIL, CUSTOMER_PASS);
		Long productId = getProductIdBySku("DRK-002", token);
		Long machineId = getMachineIdByCode("VM-LIS-001", token);
		String idempotencyKey = "idem-key-e2e-001";

		String firstSaleId =
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
										"tok_e2e_idem",
										"idempotencyKey",
										idempotencyKey))
						.post("/api/sales/purchase")
						.then()
						.statusCode(201)
						.body("status", org.hamcrest.Matchers.equalTo("COMPLETED"))
						.extract()
						.path("saleId");

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
								"tok_e2e_idem",
								"idempotencyKey",
								idempotencyKey))
				.post("/api/sales/purchase")
				.then()
				.statusCode(201)
				.body("status", org.hamcrest.Matchers.equalTo("DUPLICATE"))
				.body("saleId", org.hamcrest.Matchers.equalTo(firstSaleId));
	}
}
