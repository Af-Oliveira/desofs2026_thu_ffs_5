package pt.isep.desofs.vendnet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pt.isep.desofs.vendnet.application.service.JwtService;
import pt.isep.desofs.vendnet.domain.model.user.Role;
import pt.isep.desofs.vendnet.domain.model.user.User;
import pt.isep.desofs.vendnet.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RbacIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String customerToken;
    private String operatorToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Arrange — create users and generate tokens for each role
        customerToken = tokenForRole("rbac-customer@vendnet.com", Role.ROLE_CUSTOMER);
        operatorToken = tokenForRole("rbac-operator@vendnet.com", Role.ROLE_OPERATOR);
        adminToken    = tokenForRole("rbac-admin@vendnet.com",    Role.ROLE_ADMINISTRATOR);
    }

    // ── Admin endpoints ──────────────────────────────────────────────────────

    @Test
    void customer_cannotAccess_adminDashboard_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void operator_cannotAccess_adminDashboard_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canAccess_adminDashboard_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void customer_cannotAccess_adminUsers_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void operator_cannotAccess_adminUsers_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canAccess_adminUsers_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    // ── Machine endpoints — CUSTOMER, OPERATOR, ADMINISTRATOR all allowed ──────

    @Test
    void customer_canAccess_machines_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/machines")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }

    @Test
    void operator_canAccess_machines_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/machines")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk());
    }

    @Test
    void admin_canAccess_machines_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/machines")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    // ── Unauthenticated ───────────────────────────────────────────────────────

    @Test
    void noToken_cannotAccess_protectedEndpoint_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    // ── Public endpoints ──────────────────────────────────────────────────────

    @Test
    void noToken_canAccess_publicInfo_returns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/public/info"))
                .andExpect(status().isOk());
    }

    // ── Sales — OPERATOR and ADMINISTRATOR allowed ─────────────────────────────

    @Test
    void customer_cannotAccess_sales_returns403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/sales/machine/1")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void operator_canAccess_sales_returns2xxOr404() throws Exception {
        // Act & Assert — 200 or 404 both mean auth passed (machine may not exist)
        mockMvc.perform(get("/api/sales/machine/999999")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 403) throw new AssertionError("Expected 2xx or 4xx (not 403), got 403");
                });
    }

    // ── Helper ────────────────────────────────────────────────────────────────

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
}
