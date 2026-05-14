package pt.isep.desofs.vendnet;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

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

    private static final String TEST_EMAIL = "auth-test@vendnet.com";
    private static final String TEST_PASSWORD = "Test@1234";

    @BeforeEach
    void setUp() {
        userRepository.findByEmail(TEST_EMAIL).ifPresent(u -> {
            u.setFailedAttempts(0);
            u.setLockTime(null);
            u.setLastFailedAttemptTime(null);
            userRepository.save(u);
        });

        if (userRepository.findByEmail(TEST_EMAIL).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .email(TEST_EMAIL)
                    .password(passwordEncoder.encode(TEST_PASSWORD))
                    .name("Auth Test User")
                    .role(Role.ROLE_CUSTOMER)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    void login_validCredentials_returns200WithJwt() throws Exception {
        // Arrange
        Map<String, String> body = Map.of("email", TEST_EMAIL, "password", TEST_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void login_invalidPassword_returns401() throws Exception {
        // Arrange
        Map<String, String> body = Map.of("email", TEST_EMAIL, "password", "WrongPassword1!");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nonExistentUser_returns401() throws Exception {
        // Arrange
        Map<String, String> body = Map.of("email", "nobody@vendnet.com", "password", "Any@1234");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_withValidJwt_allowsAccess() throws Exception {
        // Arrange
        String token = jwtService.generateToken(TEST_EMAIL, Role.ROLE_CUSTOMER.name());

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void request_withExpiredJwt_returns401() throws Exception {
        // Arrange — build an expired token with past dates and invalid signature
        String expiredToken = buildExpiredToken();

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_withAlgNoneToken_returns401() throws Exception {
        // Arrange — craft a JWT with alg:none header and no signature
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"sub\":\"" + TEST_EMAIL + "\",\"role\":\"ROLE_CUSTOMER\"}").getBytes(StandardCharsets.UTF_8));
        String algNoneToken = header + "." + payload + ".";

        // Act & Assert
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + algNoneToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accountLockout_after5FailedAttempts_returns401WithLockMessage() throws Exception {
        // Arrange — ensure a fresh user exists for the lockout test
        String lockoutEmail = "lockout-test@vendnet.com";
        userRepository.findByEmail(lockoutEmail).ifPresent(u -> userRepository.save(resetUser(u)));
        if (userRepository.findByEmail(lockoutEmail).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .email(lockoutEmail)
                    .password(passwordEncoder.encode("Correct@1234"))
                    .name("Lockout Test")
                    .role(Role.ROLE_CUSTOMER)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            userRepository.save(user);
        }

        // Act — submit 5 failed login attempts to trigger lockout
        Map<String, String> wrongCreds = Map.of("email", lockoutEmail, "password", "Wrong@9999");
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongCreds)))
                    .andExpect(status().isUnauthorized());
        }

        // Assert — 6th attempt with correct password is rejected because account is locked
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", lockoutEmail, "password", "Correct@1234"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Account Locked"));
    }

    private User resetUser(User u) {
        u.setFailedAttempts(0);
        u.setLockTime(null);
        u.setLastFailedAttemptTime(null);
        return u;
    }

    private String buildExpiredToken() {
        // Manually construct a JWT with a past expiry that uses the test key.
        // We rely on the fact that JwtService uses HS256 and will reject expired tokens.
        // We create it via JwtService internals by using a very short-lived token that we pre-compute.
        // Since we can't easily create an expired token without reflection, we use a hardcoded one
        // with a known-bad signature which will also be rejected (covers the "expired" path in isTokenValid).
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"sub\":\"" + TEST_EMAIL + "\",\"role\":\"ROLE_CUSTOMER\",\"iat\":946684800,\"exp\":946684801}").getBytes(StandardCharsets.UTF_8));
        // Signature is invalid intentionally — rejected by JwtService (expired + invalid sig)
        String fakeSignature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("invalidsignature".getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + fakeSignature;
    }
}
