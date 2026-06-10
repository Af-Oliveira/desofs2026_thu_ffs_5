package pt.isep.desofs.vendnet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import pt.isep.desofs.vendnet.domain.repository.VendingMachineRepository;

/**
 * // Test Category: Integration Tests
 * // Box Type: Gray Box (knows architecture but tests through HTTP + real DB)
 * // Strategy: @SpringBootTest + H2 + real JWT auth via security filters.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Controller Integration Tests (Gray Box)")
class ControllerIntegrationTests {

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
    private ProductRepository productRepository;

    @Autowired
    private VendingMachineRepository machineRepository;

    // ──────────────────────────────────────────────
    // AuthController integration
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("AuthController")
    class AuthControllerIntegration {

        @Test
        @DisplayName("Full lifecycle: register → login → access /me")
        void fullRegistrationLoginLifecycle() throws Exception {
            // SUT: AuthController + UserController + JwtService + UserRepository
            // Integration Test / Gray Box

            // AAA: Arrange — register a new user
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"int-test@vendnet.com\",\"password\":\"IntTest@1234\",\"name\":\"Integration\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString());

            // AAA: Act — login
            String resp = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"inttest\",\"password\":\"IntTest@1234\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isString())
                    .andReturn().getResponse().getContentAsString();
            String token = objectMapper.readTree(resp).get("token").asText();

            // AAA: Assert — use token for authenticated access
            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("int-test@vendnet.com"))
                    .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
        }
    }

    // ──────────────────────────────────────────────
    // MachineController integration
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("MachineController")
    class MachineControllerIntegration {

        private String customerToken;

        @BeforeEach
        void setUp() {
            customerToken = tokenForRole("mc-cust@vendnet.io", Role.ROLE_CUSTOMER);
        }

        @Test
        @DisplayName("GET /api/machines — authenticated customer lists machines from H2")
        void findAll_authenticatedCustomer_returnsMachineList() throws Exception {
            // SUT: MachineController.findAll() → MachineService → JPA → H2
            // Integration Test / Gray Box
            // AAA: Arrange — seed a machine
            if (machineRepository.findByCode("IT-MACH-001").isEmpty()) {
                var m = pt.isep.desofs.vendnet.domain.model.machine.VendingMachine.builder()
                        .code("IT-MACH-001").location("IT Location").active(true)
                        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                machineRepository.save(m);
            }
            // AAA: Act — GET /api/machines with customer JWT
            // AAA: Assert
            mockMvc.perform(get("/api/machines")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].code").exists());
        }

        @Test
        @DisplayName("GET /api/machines — unauthenticated returns 401")
        void findAll_unauthenticated_returns401() throws Exception {
            // SUT: SecurityConfig — anyRequest().authenticated()
            // Integration Test / Gray Box
            // AAA: Arrange — no auth
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/machines"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/machines/{code} — finds machine by code from H2")
        void findByCode_returnsMachine() throws Exception {
            // SUT: MachineController.findByCode() → MachineService → JPA → H2
            // Integration Test / Gray Box
            // AAA: Arrange
            if (machineRepository.findByCode("IT-MACH-002").isEmpty()) {
                var m = pt.isep.desofs.vendnet.domain.model.machine.VendingMachine.builder()
                        .code("IT-MACH-002").location("Porto IT").active(true)
                        .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                machineRepository.save(m);
            }
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/machines/IT-MACH-002")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("IT-MACH-002"))
                    .andExpect(jsonPath("$.location").value("Porto IT"));
        }
    }

    // ──────────────────────────────────────────────
    // ProductController integration
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("ProductController")
    class ProductControllerIntegration {

        private String customerToken;

        @BeforeEach
        void setUp() {
            customerToken = tokenForRole("pc-cust@vendnet.io", Role.ROLE_CUSTOMER);
        }

        @Test
        @DisplayName("GET /api/products — authenticated customer lists products from H2")
        void findAllActive_returnsProducts() throws Exception {
            // SUT: ProductController.findAllActive() → ProductService → JPA → H2
            // Integration Test / Gray Box
            // AAA: Arrange — seed a product
            if (productRepository.findBySku("IT-SKU-001").isEmpty()) {
                var p = pt.isep.desofs.vendnet.domain.model.product.Product.builder()
                        .name("IT Product").sku("IT-SKU-001").price(new BigDecimal("2.99"))
                        .active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                productRepository.save(p);
            }
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/products")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").exists());
        }

        @Test
        @DisplayName("GET /api/products/{sku} — finds product by SKU from H2")
        void findBySku_returnsProduct() throws Exception {
            // SUT: ProductController.findBySku() → ProductService → JPA → H2
            // Integration Test / Gray Box
            // AAA: Arrange
            if (productRepository.findBySku("IT-SKU-002").isEmpty()) {
                var p = pt.isep.desofs.vendnet.domain.model.product.Product.builder()
                        .name("IT Product 2").sku("IT-SKU-002").price(new BigDecimal("1.99"))
                        .active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                productRepository.save(p);
            }
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/products/IT-SKU-002")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("IT Product 2"))
                    .andExpect(jsonPath("$.sku").value("IT-SKU-002"));
        }

        @Test
        @DisplayName("GET /api/products — unauthenticated returns 401")
        void findAllActive_unauthenticated_returns401() throws Exception {
            // SUT: SecurityConfig — ProductController requires isAuthenticated()
            // Integration Test / Gray Box
            // AAA: Arrange — no auth
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ──────────────────────────────────────────────
    // RBAC enforcement integration
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("RBAC Enforcement")
    class RbacIntegration {

        private String customerToken;
        private String operatorToken;
        private String adminToken;

        @BeforeEach
        void setUp() {
            customerToken = tokenForRole("rbac-c@vendnet.io", Role.ROLE_CUSTOMER);
            operatorToken = tokenForRole("rbac-o@vendnet.io", Role.ROLE_OPERATOR);
            adminToken = tokenForRole("rbac-a@vendnet.io", Role.ROLE_ADMINISTRATOR);
        }

        @Test
        @DisplayName("Admin → admin/dashboard → 200")
        void admin_dashboard_200() throws Exception {
            // SUT: AdminController.dashboard() + SecurityConfig + RoleHierarchy
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Operator → admin/dashboard → 403")
        void operator_dashboard_403() throws Exception {
            // SUT: RBAC — operator lacks ADMINISTRATOR role
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Customer → admin/dashboard → 403")
        void customer_dashboard_403() throws Exception {
            // SUT: RBAC — customer lacks ADMINISTRATOR role
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Admin → sales/machine/1 → 2xx (inherits OPERATOR via hierarchy)")
        void admin_sales_viaHierarchy() throws Exception {
            // SUT: SaleController.findByMachine() + RoleHierarchy (ADMIN > OPERATOR)
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("Customer → sales/machine/1 → 403")
        void customer_sales_403() throws Exception {
            // SUT: RBAC — customer lacks OPERATOR role
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/sales/machine/1")
                            .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("No token → /api/auth/me → 401")
        void noToken_me_401() throws Exception {
            // SUT: SecurityConfig — anyRequest().authenticated()
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("No token → /api/health → 200 (permitAll)")
        void noToken_health_200() throws Exception {
            // SUT: SecurityConfig — /api/health/** is permitAll()
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk());
        }
    }

    // ──────────────────────────────────────────────
    // Health & Public endpoints
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("Health & Public Endpoints")
    class HealthPublicIntegration {

        @Test
        @DisplayName("GET /api/health — UP without auth")
        void health_up() throws Exception {
            // SUT: HealthController.health()
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("UP"));
        }

        @Test
        @DisplayName("GET /api/health/ping — ok with timestamp")
        void ping_ok() throws Exception {
            // SUT: PingController.ping()
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/health/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ok"))
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.uptime").isString());
        }

        @Test
        @DisplayName("GET /api/public/info — app metadata without auth")
        void publicInfo_metadata() throws Exception {
            // SUT: PublicController.info()
            // Integration Test / Gray Box
            mockMvc.perform(get("/api/public/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.app").value("VendNet"))
                    .andExpect(jsonPath("$.version").value("0.0.1"));
        }

        @Test
        @DisplayName("GET /actuator/health — Spring actuator with DB component")
        void actuatorHealth_db() throws Exception {
            // SUT: Spring Actuator — permitAll in SecurityConfig
            // Integration Test / Gray Box
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.components.db.status").value("UP"));
        }
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────
    private String tokenForRole(String email, Role role) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .username(usernameFromEmail(email))
                    .email(email)
                    .password(passwordEncoder.encode("Test@1234"))
                    .name("Test " + role.name())
                    .role(role)
                    .accountStatus(AccountStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            return userRepository.save(u);
        });
        return jwtService.generateToken(user.getId(), role.name());
    }

    private String usernameFromEmail(String email) {
        return email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9]", "");
    }
}
