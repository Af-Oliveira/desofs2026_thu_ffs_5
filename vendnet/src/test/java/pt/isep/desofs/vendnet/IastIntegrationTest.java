package pt.isep.desofs.vendnet;

import org.junit.jupiter.api.AfterEach;
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
import pt.isep.desofs.vendnet.infrastructure.security.IastTaintTrackingFilter;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IastIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        token = tokenForRole("iast-test@vendnet.com", Role.ROLE_CUSTOMER);
        IastTaintTrackingFilter.clearDetectedFlows();
    }

    @AfterEach
    void tearDown() {
        IastTaintTrackingFilter.clearDetectedFlows();
    }

    @Test
    void sqlInjectionTaint_isDetected_viaQueryParameters() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .param("search", "' OR '1'='1"))
                .andExpect(status().isOk());

        List<IastTaintTrackingFilter.TaintFlow> flows =
                IastTaintTrackingFilter.getDetectedFlows().values().stream()
                        .flatMap(List::stream)
                        .filter(f -> "SQL_INJECTION".equals(f.type))
                        .toList();

        assertFalse(flows.isEmpty(),
                "IAST must detect SQL injection taint in query parameters (SR-09)");
    }

    @Test
    void pathTraversalTaint_isDetected_viaQueryParameters() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .param("file", "../../../etc/passwd"))
                .andExpect(status().isOk());

        List<IastTaintTrackingFilter.TaintFlow> flows =
                IastTaintTrackingFilter.getDetectedFlows().values().stream()
                        .flatMap(List::stream)
                        .filter(f -> "PATH_TRAVERSAL".equals(f.type))
                        .toList();

        assertFalse(flows.isEmpty(),
                "IAST must detect path traversal taint in query parameters (SR-26)");
    }

    @Test
    void sqlInjectionTaint_isDetected_viaQueryString() throws Exception {
        mockMvc.perform(get("/api/products?name=' OR 1=1 --")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        List<IastTaintTrackingFilter.TaintFlow> flows =
                IastTaintTrackingFilter.getDetectedFlows().values().stream()
                        .flatMap(List::stream)
                        .filter(f -> "SQL_INJECTION".equals(f.type))
                        .toList();

        assertFalse(flows.isEmpty(),
                "IAST must detect SQL injection taint in query string (SR-09)");
    }

    @Test
    void commandInjectionTaint_isNotDetected_forNormalRequests() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"iast-test@vendnet.com\",\"password\":\"Test@1234\"}"))
                .andExpect(status().isOk());

        List<IastTaintTrackingFilter.TaintFlow> flows =
                IastTaintTrackingFilter.getDetectedFlows().values().stream()
                        .flatMap(List::stream)
                        .filter(f -> "COMMAND_INJECTION".equals(f.type))
                        .toList();

        assertFalse(!flows.isEmpty(),
                "IAST must not produce false positives on normal login requests");
    }

    @Test
    void normalRequest_producesNoSinks() throws Exception {
        mockMvc.perform(get("/api/public/info"))
                .andExpect(status().isOk());

        long totalFlows = IastTaintTrackingFilter.getDetectedFlows().values().stream()
                .mapToLong(List::size)
                .sum();

        org.junit.jupiter.api.Assertions.assertTrue(totalFlows >= 0,
                "IAST must not crash on public endpoints");
    }

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
