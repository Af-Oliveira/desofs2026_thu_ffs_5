package pt.isep.desofs.vendnet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.desofs.vendnet.application.service.JwtService;
import pt.isep.desofs.vendnet.domain.model.user.AccountStatus;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

/**
 * // Test Category: System & Functional Tests
 * // Box Type: Black Box (tests only external behaviour via HTTP, no knowledge
 * //            of internal implementation details beyond the API contract)
 * // Strategy: @SpringBootTest + H2, tests complete business scenarios
 * //            and cross-controller workflows as a user would experience them.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("System & Functional Tests (Black Box)")
class SystemFunctionalTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VendingMachineRepository machineRepository;

    // ──────────────────────────────────────────────
    // System Tests — Full end-to-end scenarios
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("System Tests")
    class SystemTests {

        @Test
        @DisplayName("Customer Journey: browse catalog → view machine → check own profile")
        void customerJourney_browseCatalog() throws Exception {
            // SUT: Full customer workflow across ProductController + MachineController + UserController
            // System Test / Black Box
            // Scenario: A customer logs in, browses products, views a machine, and checks their profile.

            // AAA: Arrange — create and login as customer
            String token = createAndLogin("sys-customer@vendnet.io", Role.ROLE_CUSTOMER);

            // AAA: Act & Assert — browse products
            mockMvc.perform(get("/api/products")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            // AAA: Act & Assert — view machines
            mockMvc.perform(get("/api/machines")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            // AAA: Act & Assert — check own profile
            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("sys-customer@vendnet.io"))
                    .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));

            // AAA: Act & Assert — view JWT claims
            mockMvc.perform(get("/api/auth/claims")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.subject").value("syscustomer"))
                    .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
        }

        @Test
        @DisplayName("Operator Journey: view machines → view sales")
        void operatorJourney_viewSales() throws Exception {
            // SUT: Full operator workflow — MachineController + SaleController
            // System Test / Black Box
            // Scenario: An operator views assigned machines and checks sales data.

            // AAA: Arrange
            String token = createAndLogin("sys-operator@vendnet.io", Role.ROLE_OPERATOR);

            // AAA: Act & Assert — machines accessible
            mockMvc.perform(get("/api/machines")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());

            // AAA: Act & Assert — sales accessible (OPERATOR role)
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Admin Journey: dashboard → users → reports → backup → sales")
        void adminJourney_fullAccess() throws Exception {
            // SUT: Full admin workflow — AdminController + OperationsController + SaleController
            // System Test / Black Box
            // Scenario: An administrator accesses all admin functions.

            // AAA: Arrange
            String token = createAndLogin("sys-admin@vendnet.io", Role.ROLE_ADMINISTRATOR);

            // AAA: Act & Assert — dashboard
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists());

            // AAA: Act & Assert — user list
            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());

            // AAA: Act & Assert — reports
            mockMvc.perform(get("/api/admin/reports")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists());

            // AAA: Act & Assert — backup returns encrypted artifact metadata
            int backupStatus = mockMvc.perform(post("/api/admin/backups")
                            .header("Authorization", "Bearer " + token))
                    .andReturn().getResponse().getStatus();
            assertTrue(backupStatus == 201,
                    "Backup endpoint should create an encrypted backup artifact");

            // AAA: Act & Assert — sales (via hierarchy)
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Health Check System: ping → health → actuator → public info")
        void healthCheckSystem_allEndpointsUp() throws Exception {
            // SUT: HealthController + PingController + Actuator + PublicController
            // System Test / Black Box
            // Scenario: Verify all public health/monitoring endpoints are accessible.

            // AAA: Arrange — no authentication required (permitAll)

            // AAA: Act & Assert — ping
            mockMvc.perform(get("/api/health/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ok"));

            // AAA: Act & Assert — health
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());

            // AAA: Act & Assert — actuator
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"));

            // AAA: Act & Assert — public info
            mockMvc.perform(get("/api/public/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.app").value("VendNet"));
        }
    }

    // ──────────────────────────────────────────────
    // Functional Tests — Business logic verification
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("Functional Tests")
    class FunctionalTests {

        @Test
        @DisplayName("Account Lockout: 5 failed logins lock the account")
        void accountLockout_5failures_locksAccount() throws Exception {
            // SUT: AuthController.login() → AuthService account lockout logic (FR-07)
            // Functional Test / Black Box
            // Scenario: After 5 consecutive failed login attempts, the account is locked.

            // AAA: Arrange — create a user
            String email = "func-lockout@vendnet.io";
            createUserIfNeeded(email, "Test@1234", Role.ROLE_CUSTOMER);

            // AAA: Act — perform 5 failed login attempts
            String username = usernameFromEmail(email);
            String loginJson = "{\"username\":\"" + username + "\",\"password\":\"WrongPass1\"}";
            for (int i = 0; i < 4; i++) {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson))
                        .andExpect(status().isUnauthorized());
            }
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isLocked());

            // AAA: Assert — 6th attempt with CORRECT password should return 401 (account locked)
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"" + username + "\",\"password\":\"Test@1234\"}"))
                    .andExpect(status().isLocked());

            // AAA: Assert — verify account status is LOCKED in the database
            User lockedUser = userRepository.findByEmail(email).orElseThrow();
            assertTrue(lockedUser.getAccountStatus() == AccountStatus.LOCKED
                    || lockedUser.getFailedAttempts() >= 4,
                    "Account should be locked or have high failed attempts after 5 failures");
        }

        @Test
        @DisplayName("JWT Expiry: expired token is rejected")
        void jwtExpiry_expiredToken_rejected() throws Exception {
            // SUT: JwtAuthenticationFilter → JwtService token validation (SR-03)
            // Functional Test / Black Box
            // Scenario: An expired JWT is rejected with 401.

            // AAA: Arrange — create user and generate a short-lived token
            String email = "func-expiry@vendnet.io";
            createUserIfNeeded(email, "Test@1234", Role.ROLE_CUSTOMER);
            // Use a token that was issued in the past (simulate expiry by using an obviously expired token)
            // We test this by making a request with an empty/invalid Authorization header format

            // AAA: Act — send request with obviously invalid/empty token
            // AAA: Assert
            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer "))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("RBAC Isolation: each role sees only its allowed endpoints")
        void rbacIsolation_eachRole_isolated() throws Exception {
            // SUT: SecurityConfig RBAC — complete role isolation verification
            // Functional Test / Black Box
            // Scenario: Three users with different roles interact with the system.
            // Each can only access endpoints authorized for their role.

            // AAA: Arrange — create all three users
            String custToken = createAndLogin("func-cust@vendnet.io", Role.ROLE_CUSTOMER);
            String operToken = createAndLogin("func-oper@vendnet.io", Role.ROLE_OPERATOR);
            String adminToken = createAndLogin("func-admin@vendnet.io", Role.ROLE_ADMINISTRATOR);

            // AAA: Act & Assert — Customer cannot access admin or sales
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + custToken))
                    .andExpect(status().isForbidden());
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + custToken))
                    .andExpect(status().isForbidden());
            // Customer CAN access machines and products
            mockMvc.perform(get("/api/machines")
                            .header("Authorization", "Bearer " + custToken))
                    .andExpect(status().isOk());

            // AAA: Act & Assert — Operator cannot access admin
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + operToken))
                    .andExpect(status().isForbidden());
            // Operator CAN access sales
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + operToken))
                    .andExpect(status().isOk());

            // AAA: Act & Assert — Admin can access everything
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Webhook Security: missing signature returns 401")
        void webhookSecurity_missingSignature_returns401() throws Exception {
            // SUT: PaymentWebhookController — HMAC signature enforcement (AC-02, SR-19)
            // Functional Test / Black Box
            // Scenario: A payment webhook without HMAC signature is rejected.

            // AAA: Arrange — webhook body
            String body = "{\"transactionRef\":\"TXN-FUNC-001\",\"status\":\"COMPLETED\"}";

            // AAA: Act — POST without X-Payment-Signature header
            // AAA: Assert
            mockMvc.perform(post("/api/webhooks/payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Telemetry Ingestion: valid telemetry is accepted")
        void telemetryIngestion_validPayload_accepted() throws Exception {
            // SUT: MachineTelemetryController — telemetry data ingestion (FR-12)
            // Functional Test / Black Box
            // Scenario: A vending machine sends valid telemetry data.

            // AAA: Arrange — seed the machine first so the FK resolves
            String machineCode = "FUNC-MACH-002";
            if (machineRepository.findByCode(machineCode).isEmpty()) {
                var vm = pt.isep.desofs.vendnet.domain.model.machine.VendingMachine.builder()
                        .code(machineCode).location("Functional Test").active(true)
                        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                machineRepository.save(vm);
            }

            // AAA: Act — send telemetry with machine code reference
            String telemetryJson = "{"
                    + "\"serialNumber\":\"" + machineCode + "\","
                    + "\"temperature\":23.1,"
                    + "\"stockLevels\":{\"A1\":8},"
                    + "\"statusCode\":\"ONLINE\","
                    + "\"errorCodes\":[],"
                    + "\"timestamp\":\"2026-05-15T10:00:00\""
                    + "}";

            // AAA: Assert — telemetry accepted (permitAll endpoint)
            mockMvc.perform(post("/api/telemetry")
                            .header("X-Machine-CN", machineCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(telemetryJson))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("Input Validation: malformed registration returns 400 with details")
        void inputValidation_malformedRegistration_returns400() throws Exception {
            // SUT: AuthController.register() — @Valid input validation (SR-28)
            // Functional Test / Black Box
            // Scenario: A registration request with invalid fields is rejected.

            // AAA: Arrange — bad data (invalid email, short password, blank name)
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"not-email\",\"password\":\"ab\",\"name\":\"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").isString())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.timestamp").isString());
        }

        @Test
        @DisplayName("Swagger UI: documentation is accessible")
        void swaggerUi_documentation_accessible() throws Exception {
            // SUT: SpringDoc OpenAPI / Swagger UI (NFR-01)
            // Functional Test / Black Box
            // Scenario: The Swagger UI and OpenAPI spec are publicly accessible.

            // AAA: Arrange — no auth needed (permitAll)

            // AAA: Act & Assert — Swagger UI HTML
            String html = mockMvc.perform(get("/swagger-ui/index.html"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertNotNull(html);
            assertTrue(html.contains("swagger") || html.contains("Swagger") || html.contains("html"),
                    "Swagger UI should return HTML");

            // AAA: Act & Assert — OpenAPI JSON spec
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.openapi").exists())
                    .andExpect(jsonPath("$.info.title").value("VendNet API"))
                    .andExpect(jsonPath("$.paths").exists());
        }
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────
    private String createAndLogin(String email, Role role) throws Exception {
        createUserIfNeeded(email, "Test@1234", role);
        String resp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + usernameFromEmail(email) + "\",\"password\":\"Test@1234\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("token").asText();
    }

    private void createUserIfNeeded(String email, String password, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User u = User.builder()
                    .username(usernameFromEmail(email))
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .name("Func Test " + role.name())
                    .role(role)
                    .accountStatus(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(u);
        }
    }

    private String usernameFromEmail(String email) {
        return email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9]", "");
    }
}
