package pt.isep.desofs.vendnet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pt.isep.desofs.vendnet.api.controller.AdminController;
import pt.isep.desofs.vendnet.api.controller.AuthController;
import pt.isep.desofs.vendnet.api.controller.HealthController;
import pt.isep.desofs.vendnet.api.controller.MachineController;
import pt.isep.desofs.vendnet.api.controller.OperationsController;
import pt.isep.desofs.vendnet.api.controller.PingController;
import pt.isep.desofs.vendnet.api.controller.ProductController;
import pt.isep.desofs.vendnet.api.controller.PublicController;
import pt.isep.desofs.vendnet.api.controller.SaleController;
import pt.isep.desofs.vendnet.api.controller.UserController;
import pt.isep.desofs.vendnet.api.dto.AuthResponse;
import pt.isep.desofs.vendnet.api.dto.ClaimsResponse;
import pt.isep.desofs.vendnet.api.dto.UserResponse;
import pt.isep.desofs.vendnet.application.service.AuthService;
import pt.isep.desofs.vendnet.application.service.MachineService;
import pt.isep.desofs.vendnet.application.service.ProductService;
import pt.isep.desofs.vendnet.application.service.SaleService;
import pt.isep.desofs.vendnet.application.service.UserManagementService;
import pt.isep.desofs.vendnet.domain.model.machine.VendingMachine;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.model.sale.Sale;
import pt.isep.desofs.vendnet.infrastructure.os.BackupResult;
import pt.isep.desofs.vendnet.infrastructure.os.BackupService;
import pt.isep.desofs.vendnet.infrastructure.os.ReportDirectoryService;

/**
 * // Test Category: Unit Tests
 * // Box Type: White Box (full knowledge of controller internals, mocked dependencies)
 * // Strategy: MockMvc standalone — each controller tested in isolation with all services mocked.
 *
 * <p>Each test method documents: SUT (System Under Test), AAA phases, test type, and box type.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Controller Unit Tests (White Box)")
class ControllerUnitTests {

    private MockMvc mockMvc;

    // ──────────────────────────────────────────────
    // AuthController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("AuthController")
    class AuthControllerUnitTests {

        @Mock
        private AuthService authService;

        @InjectMocks
        private AuthController authController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        }

        @Test
        @DisplayName("POST /api/auth/login — valid credentials returns 200 with token")
        void login_validCredentials_returns200WithToken() throws Exception {
            // SUT: AuthController.login(LoginRequest)
            // Unit Test / White Box
            // AAA: Arrange — mock service returns AuthResponse with token
            AuthResponse mockResponse = AuthResponse.builder()
                    .token("jwt-token-abc123")
                    .email("user@vendnet.io")
                    .name("Test User")
                    .role("ROLE_CUSTOMER")
                    .mfaRequired(false)
                    .build();
            when(authService.login(any())).thenReturn(mockResponse);

            // AAA: Act — POST /api/auth/login
            // AAA: Assert — expect 200, token, email, role
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"user\",\"password\":\"Test@1234\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-abc123"))
                    .andExpect(jsonPath("$.email").value("user@vendnet.io"))
                    .andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"))
                    .andExpect(jsonPath("$.mfaRequired").value(false));
        }

        @Test
        @DisplayName("POST /api/auth/register — new user returns 200 with token")
        void register_newUser_returns200WithToken() throws Exception {
            // SUT: AuthController.register(RegisterRequest)
            // Unit Test / White Box
            // AAA: Arrange
            AuthResponse mockResponse = AuthResponse.builder()
                    .token("jwt-token-new")
                    .email("new@vendnet.io")
                    .name("New User")
                    .role("ROLE_CUSTOMER")
                    .mfaRequired(false)
                    .build();
            when(authService.register(any())).thenReturn(mockResponse);

            // AAA: Act — POST /api/auth/register
            // AAA: Assert
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"new@vendnet.io\",\"password\":\"NewUser@1234\",\"name\":\"New User\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-new"))
                    .andExpect(jsonPath("$.email").value("new@vendnet.io"))
                    .andExpect(jsonPath("$.name").value("New User"));
        }

        @Test
        @DisplayName("POST /api/auth/register — invalid input returns 400")
        void register_invalidInput_returns400() throws Exception {
            // SUT: AuthController.register(RegisterRequest) with @Valid
            // Unit Test / White Box
            // AAA: Arrange — invalid email, short password, blank name
            // AAA: Act — POST /api/auth/register with bad data
            // AAA: Assert — expect 400 (validation fails before service call)
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"bad\",\"password\":\"ab\",\"name\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ──────────────────────────────────────────────
    // AdminController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("AdminController")
    class AdminControllerUnitTests {

        @Mock
        private UserManagementService userManagementService;

        @InjectMocks
        private AdminController adminController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        }

        @Test
        @DisplayName("GET /api/admin/dashboard — returns welcome message")
        void dashboard_returnsWelcomeMessage() throws Exception {
            // SUT: AdminController.dashboard()
            // Unit Test / White Box
            // AAA: Arrange
            when(userManagementService.getDashboard()).thenReturn(
                    java.util.Map.of("message", "Welcome to the admin dashboard",
                            "totalUsers", 3, "activeUsers", 2, "lockedUsers", 1, "suspendedUsers", 0));
            // AAA: Act — GET /api/admin/dashboard
            // AAA: Assert
            mockMvc.perform(get("/api/admin/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Welcome to the admin dashboard"))
                    .andExpect(jsonPath("$.totalUsers").value(3));
        }

        @Test
        @DisplayName("GET /api/admin/users — returns user list")
        void listUsers_returnsAdminUserList() throws Exception {
            // SUT: AdminController.listUsers()
            // Unit Test / White Box
            // AAA: Arrange
            UserResponse user = UserResponse.builder()
                    .id(1L).email("admin@vendnet.io").name("Admin")
                    .role("ROLE_ADMINISTRATOR").createdAt(LocalDateTime.now()).build();
            when(userManagementService.listUsers()).thenReturn(java.util.List.of(user));
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email").value("admin@vendnet.io"))
                    .andExpect(jsonPath("$[0].role").value("ROLE_ADMINISTRATOR"));
        }

        @Test
        @DisplayName("GET /api/admin/reports — returns reports access confirmation")
        void reports_returnsReportsAccess() throws Exception {
            // SUT: AdminController.reports()
            // Unit Test / White Box
            // AAA: Arrange
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/admin/reports"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Reports accessible by admin only"))
                    .andExpect(jsonPath("$.auth").value("hasRole('ADMINISTRATOR')"));
        }
    }

    // ──────────────────────────────────────────────
    // MachineController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("MachineController")
    class MachineControllerUnitTests {

        @Mock
        private MachineService machineService;

        @InjectMocks
        private MachineController machineController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(machineController).build();
        }

        @Test
        @DisplayName("GET /api/machines — returns list of machines")
        void findAll_returnsMachineList() throws Exception {
            // SUT: MachineController.findAll()
            // Unit Test / White Box
            // AAA: Arrange
            VendingMachine vm = VendingMachine.builder()
                    .id(1L).code("VM-001").location("Lisbon").active(true)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(machineService.findAll()).thenReturn(List.of(vm));

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/machines"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].code").value("VM-001"))
                    .andExpect(jsonPath("$[0].location").value("Lisbon"))
                    .andExpect(jsonPath("$[0].active").value(true));
        }

        @Test
        @DisplayName("GET /api/machines — empty list returns 200 with []")
        void findAll_emptyList_returns200() throws Exception {
            // SUT: MachineController.findAll()
            // Unit Test / White Box
            // AAA: Arrange
            when(machineService.findAll()).thenReturn(Collections.emptyList());

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/machines"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("GET /api/machines/{code} — returns single machine")
        void findByCode_returnsMachine() throws Exception {
            // SUT: MachineController.findByCode(String)
            // Unit Test / White Box
            // AAA: Arrange
            VendingMachine vm = VendingMachine.builder()
                    .id(2L).code("VM-PTO-001").location("Porto").active(true)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(machineService.findByCode("VM-PTO-001")).thenReturn(vm);

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/machines/VM-PTO-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("VM-PTO-001"))
                    .andExpect(jsonPath("$.location").value("Porto"));
        }
    }

    // ──────────────────────────────────────────────
    // ProductController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("ProductController")
    class ProductControllerUnitTests {

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        }

        @Test
        @DisplayName("GET /api/products — returns active products list")
        void findAllActive_returnsProductList() throws Exception {
            // SUT: ProductController.findAllActive()
            // Unit Test / White Box
            // AAA: Arrange
            Product p = Product.builder()
                    .id(1L).name("Cola").sku("DRK-001").price(new BigDecimal("1.50"))
                    .active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(productService.findAllActive()).thenReturn(List.of(p));

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Cola"))
                    .andExpect(jsonPath("$[0].sku").value("DRK-001"))
                    .andExpect(jsonPath("$[0].price").value(1.50))
                    .andExpect(jsonPath("$[0].active").value(true));
        }

        @Test
        @DisplayName("GET /api/products/{sku} — returns product by SKU")
        void findBySku_returnsProduct() throws Exception {
            // SUT: ProductController.findBySku(String)
            // Unit Test / White Box
            // AAA: Arrange
            Product p = Product.builder()
                    .id(2L).name("Chips").sku("SNK-001").price(new BigDecimal("1.20"))
                    .active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(productService.findBySku("SNK-001")).thenReturn(p);

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/products/SNK-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Chips"))
                    .andExpect(jsonPath("$.sku").value("SNK-001"));
        }
    }

    // ──────────────────────────────────────────────
    // SaleController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("SaleController")
    class SaleControllerUnitTests {

        @Mock
        private SaleService saleService;

        @Mock
        private AuthService authService;

        @InjectMocks
        private SaleController saleController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(saleController).build();
        }

        @Test
        @DisplayName("GET /api/sales/machine/{id} — returns sales list")
        void findByMachine_returnsSalesList() throws Exception {
            VendingMachine vm = VendingMachine.builder().id(1L).code("VM-LIS-001").location("Lisbon")
                    .active(true).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            Product p = Product.builder().id(1L).name("Cola").sku("DRK-001")
                    .price(new BigDecimal("1.50")).active(true)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            Sale sale = Sale.builder().id(1L).machine(vm).product(p)
                    .price(new BigDecimal("1.50")).quantity(2).totalAmount(new BigDecimal("3.00")).unitPrice(new BigDecimal("1.50"))
                    .saleDate(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();
            when(saleService.findByMachineId(1L)).thenReturn(List.of(sale));

            mockMvc.perform(get("/api/sales/machine/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].price").value(1.50))
                    .andExpect(jsonPath("$[0].quantity").value(2));
        }

        @Test
        @DisplayName("GET /api/sales/machine/{id} — empty list returns 200")
        void findByMachine_emptyList_returns200() throws Exception {
            when(saleService.findByMachineId(999L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/sales/machine/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ──────────────────────────────────────────────
    // OperationsController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("OperationsController")
    class OperationsControllerUnitTests {

        @Mock
        private BackupService backupService;

        @Mock
        private ReportDirectoryService reportDirectoryService;

        @InjectMocks
        private OperationsController operationsController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(operationsController).build();
        }

        @Test
        @DisplayName("POST /api/admin/operations/backup — triggers backup")
        void triggerBackup_returnsInitiated() throws Exception {
            // SUT: OperationsController.triggerBackup()
            // Unit Test / White Box
            // AAA: Arrange
            when(backupService.generateBackup())
                    .thenReturn(BackupResult.builder()
                            .filename("vendnet-backup.sql.enc")
                            .size(42L)
                            .checksum("a".repeat(64))
                            .timestamp(LocalDateTime.now())
                            .build());
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(post("/api/admin/operations/backup"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.filename").exists())
                    .andExpect(jsonPath("$.checksum").exists());
        }

        @Test
        @DisplayName("POST /api/admin/operations/reports/sales — generates sales report")
        void generateSalesReport_returnsPath() throws Exception {
            // SUT: OperationsController.generateSalesReport()
            // Unit Test / White Box
            // AAA: Arrange
            String reportPath = "/var/vendnet/reports/sales/2026/05/15";
            when(reportDirectoryService.createReportDirectory("sales")).thenReturn(reportPath);

            // AAA: Act
            // AAA: Assert
            mockMvc.perform(post("/api/admin/operations/reports/sales"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("report generated"))
                    .andExpect(jsonPath("$.path").value(reportPath));
        }
    }

    // ──────────────────────────────────────────────
    // UserController unit tests
    // ──────────────────────────────────────────────
    // NOTE: UserController.me() and .claims() directly access
    // SecurityContextHolder.getContext().getAuthentication().
    // These require a Spring Security integration context and are
    // tested comprehensively in ControllerIntegrationTests (Gray Box).

    // ──────────────────────────────────────────────
    // HealthController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("HealthController")
    class HealthControllerUnitTests {

        private HealthController healthController;

        @BeforeEach
        void setUp() {
            healthController = new HealthController(Optional.empty());
            mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
        }

        @Test
        @DisplayName("GET /api/health — returns UP")
        void health_returnsUp() throws Exception {
            // SUT: HealthController.health()
            // Unit Test / White Box
            // AAA: Arrange — none needed
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("UP"));
        }
    }

    // ──────────────────────────────────────────────
    // PingController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("PingController")
    class PingControllerUnitTests {

        private PingController pingController;

        @BeforeEach
        void setUp() {
            pingController = new PingController(Optional.empty());
            mockMvc = MockMvcBuilders.standaloneSetup(pingController).build();
        }

        @Test
        @DisplayName("GET /api/health/ping — returns status ok with timestamp")
        void ping_returnsStatusOk() throws Exception {
            // SUT: PingController.ping()
            // Unit Test / White Box
            // AAA: Arrange — none needed
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/health/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ok"))
                    .andExpect(jsonPath("$.message").value("Hello World from VendNet!"))
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.uptime").isString());
        }
    }

    // ──────────────────────────────────────────────
    // PublicController unit tests
    // ──────────────────────────────────────────────
    @Nested
    @DisplayName("PublicController")
    class PublicControllerUnitTests {

        @InjectMocks
        private PublicController publicController;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
        }

        @Test
        @DisplayName("GET /api/public/info — returns app metadata")
        void info_returnsAppMetadata() throws Exception {
            // SUT: PublicController.info()
            // Unit Test / White Box
            // AAA: Arrange — none needed (static response from controller)
            // AAA: Act
            // AAA: Assert
            mockMvc.perform(get("/api/public/info"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.app").value("VendNet"))
                    .andExpect(jsonPath("$.version").value("0.0.1"))
                    .andExpect(jsonPath("$.desc").isString());
        }
    }
}
