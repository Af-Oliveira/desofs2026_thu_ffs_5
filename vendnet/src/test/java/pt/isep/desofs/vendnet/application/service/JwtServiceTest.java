package pt.isep.desofs.vendnet.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService("test_secret_key_for_testing_purposes_only", 3600000L);
	}

	@Test
	void constructor_shouldRejectShortSecret() {
		assertThrows(IllegalStateException.class, () -> new JwtService("short", 3600000L));
	}

	@Test
	void generateToken_shouldCreateNonEmptyToken() {
		String token = jwtService.generateToken("user@vendnet.com");

		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertEquals(3, token.split("\\.").length);
	}

	@Test
	void generateToken_withRole_shouldIncludeRole() {
		String token = jwtService.generateToken("user@vendnet.com", "ROLE_CUSTOMER");

		assertNotNull(token);
		assertEquals("ROLE_CUSTOMER", jwtService.extractRole(token));
	}

	@Test
	void extractEmail_shouldReturnSubject() {
		String token = jwtService.generateToken("user@vendnet.com");

		assertEquals("user@vendnet.com", jwtService.extractEmail(token));
	}

	@Test
	void extractRole_shouldReturnNullWhenNoRole() {
		String token = jwtService.generateToken("user@vendnet.com");

		assertNull(jwtService.extractRole(token));
	}

	@Test
	void extractRole_shouldReturnRoleWhenPresent() {
		String token = jwtService.generateToken("admin@vendnet.com", "ROLE_ADMINISTRATOR");

		assertEquals("ROLE_ADMINISTRATOR", jwtService.extractRole(token));
	}

	@Test
	void isTokenValid_shouldReturnTrueForValidToken() {
		String token = jwtService.generateToken("user@vendnet.com");

		assertTrue(jwtService.isTokenValid(token));
	}

	@Test
	void isTokenValid_shouldReturnFalseForInvalidToken() {
		assertFalse(jwtService.isTokenValid("invalid.token.here"));
	}

	@Test
	void isTokenValid_shouldReturnFalseForNullToken() {
		assertFalse(jwtService.isTokenValid(null));
	}

	@Test
	void isTokenValid_shouldReturnFalseForEmptyToken() {
		assertFalse(jwtService.isTokenValid(""));
	}

	@Test
	void isTokenValid_shouldReturnFalseForAlgNoneToken() {
		String header =
				Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"alg\":\"none\",\"typ\":\"JWT\"}"
										.getBytes(StandardCharsets.UTF_8));
		String payload =
				Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"sub\":\"user@vendnet.com\"}"
										.getBytes(StandardCharsets.UTF_8));
		String algNoneToken = header + "." + payload + ".";

		assertFalse(jwtService.isTokenValid(algNoneToken));
	}

	@Test
	void isTokenValid_shouldReturnFalseForAlgNoneWithSpace() {
		String header =
				Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"alg\": \"none\",\"typ\":\"JWT\"}"
										.getBytes(StandardCharsets.UTF_8));
		String payload =
				Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"sub\":\"user@vendnet.com\"}"
										.getBytes(StandardCharsets.UTF_8));
		String algNoneToken = header + "." + payload + ".";

		assertFalse(jwtService.isTokenValid(algNoneToken));
	}

	@Test
	void isTokenValid_shouldReturnFalseForMalformedHeader() {
		String badHeader = "!!!not_base64!!!";
		String payload =
				Base64.getUrlEncoder()
						.withoutPadding()
						.encodeToString(
								"{\"sub\":\"user@vendnet.com\"}"
										.getBytes(StandardCharsets.UTF_8));
		String token = badHeader + "." + payload + ".sig";

		assertFalse(jwtService.isTokenValid(token));
	}

	@Test
	void blocklistToken_shouldInvalidateToken() {
		String token = jwtService.generateToken("user@vendnet.com");

		assertTrue(jwtService.isTokenValid(token));

		jwtService.blocklistToken(token);

		assertFalse(jwtService.isTokenValid(token));
	}

	@Test
	void blocklistToken_shouldNotThrowOnInvalidToken() {
		assertDoesNotThrow(() -> jwtService.blocklistToken("invalid.token.value"));
	}

	@Test
	void extractJti_shouldReturnUniqueId() {
		String token1 = jwtService.generateToken("user@vendnet.com");
		String token2 = jwtService.generateToken("user@vendnet.com");

		assertNotNull(jwtService.extractJti(token1));
		assertNotNull(jwtService.extractJti(token2));
		assertNotEquals(jwtService.extractJti(token1), jwtService.extractJti(token2));
	}

	@Test
	void revokeUserTokens_shouldCleanExpiredBlocklistEntries() {
		assertDoesNotThrow(() -> jwtService.revokeUserTokens());
	}

	@Test
	void token_wrongSignature_shouldNotBeValid() {
		String[] parts = jwtService.generateToken("user@vendnet.com").split("\\.");
		String tamperedToken = parts[0] + "." + parts[1] + ".wrongsignature";

		assertFalse(jwtService.isTokenValid(tamperedToken));
	}
}
