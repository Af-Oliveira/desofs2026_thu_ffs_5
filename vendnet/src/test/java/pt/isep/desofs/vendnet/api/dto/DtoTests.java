package pt.isep.desofs.vendnet.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DtoTests {

	@Test
	void loginRequest_allArgsConstructor_shouldSetFields() {
		LoginRequest request = new LoginRequest("user@vendnet.com", "password123");

		assertEquals("user@vendnet.com", request.getEmail());
		assertEquals("password123", request.getPassword());
	}

	@Test
	void loginRequest_setters_shouldModifyFields() {
		LoginRequest request = new LoginRequest();
		request.setEmail("admin@vendnet.com");
		request.setPassword("adminPass123");

		assertEquals("admin@vendnet.com", request.getEmail());
		assertEquals("adminPass123", request.getPassword());
	}

	@Test
	void loginRequest_noArgsConstructor_shouldCreateEmpty() {
		LoginRequest request = new LoginRequest();

		assertNull(request.getEmail());
		assertNull(request.getPassword());
	}

	@Test
	void registerRequest_allArgsConstructor_shouldSetFields() {
		RegisterRequest request = new RegisterRequest("newuser@vendnet.com", "pass123456", "New User");

		assertEquals("newuser@vendnet.com", request.getEmail());
		assertEquals("pass123456", request.getPassword());
		assertEquals("New User", request.getName());
	}

	@Test
	void registerRequest_setters_shouldModifyFields() {
		RegisterRequest request = new RegisterRequest();
		request.setEmail("test@vendnet.com");
		request.setPassword("testPass123");
		request.setName("Test User");

		assertEquals("test@vendnet.com", request.getEmail());
		assertEquals("testPass123", request.getPassword());
		assertEquals("Test User", request.getName());
	}

	@Test
	void registerRequest_noArgsConstructor_shouldCreateEmpty() {
		RegisterRequest request = new RegisterRequest();

		assertNull(request.getEmail());
		assertNull(request.getPassword());
		assertNull(request.getName());
	}

	@Test
	void authResponse_builder_shouldCreateWithAllFields() {
		AuthResponse response = AuthResponse.builder()
				.token("jwt-token-here")
				.email("user@vendnet.com")
				.name("User Name")
				.role("ROLE_CUSTOMER")
				.mfaRequired(true)
				.build();

		assertEquals("jwt-token-here", response.getToken());
		assertEquals("user@vendnet.com", response.getEmail());
		assertEquals("User Name", response.getName());
		assertEquals("ROLE_CUSTOMER", response.getRole());
		assertTrue(response.isMfaRequired());
	}

	@Test
	void authResponse_builder_defaultMfaRequired_shouldBeFalse() {
		AuthResponse response = AuthResponse.builder()
				.token("token")
				.email("user@vendnet.com")
				.name("User")
				.role("ROLE_CUSTOMER")
				.build();

		assertFalse(response.isMfaRequired());
	}

	@Test
	void authResponse_setters_shouldModifyFields() {
		AuthResponse response = new AuthResponse();
		response.setToken("new-token");
		response.setEmail("admin@vendnet.com");
		response.setName("Admin");
		response.setRole("ROLE_ADMINISTRATOR");
		response.setMfaRequired(true);

		assertEquals("new-token", response.getToken());
		assertEquals("admin@vendnet.com", response.getEmail());
		assertEquals("Admin", response.getName());
		assertEquals("ROLE_ADMINISTRATOR", response.getRole());
		assertTrue(response.isMfaRequired());
	}

	@Test
	void authResponse_noArgsConstructor_shouldCreateEmpty() {
		AuthResponse response = new AuthResponse();

		assertNull(response.getToken());
		assertNull(response.getEmail());
		assertNull(response.getName());
		assertNull(response.getRole());
		assertFalse(response.isMfaRequired());
	}

	@Test
	void authResponse_allArgsConstructor_shouldCreate() {
		AuthResponse response = new AuthResponse("token", "email@vendnet.com", "Name", "ROLE_OPERATOR", true);

		assertEquals("token", response.getToken());
		assertEquals("email@vendnet.com", response.getEmail());
		assertEquals("Name", response.getName());
		assertEquals("ROLE_OPERATOR", response.getRole());
		assertTrue(response.isMfaRequired());
	}

	@Test
	void mfaVerifyRequest_allArgsConstructor_shouldSetFields() {
		MfaVerifyRequest request = new MfaVerifyRequest("admin@vendnet.com", "123456");

		assertEquals("admin@vendnet.com", request.getEmail());
		assertEquals("123456", request.getCode());
	}

	@Test
	void mfaVerifyRequest_setters_shouldModifyFields() {
		MfaVerifyRequest request = new MfaVerifyRequest();
		request.setEmail("admin@vendnet.com");
		request.setCode("654321");

		assertEquals("admin@vendnet.com", request.getEmail());
		assertEquals("654321", request.getCode());
	}

	@Test
	void mfaVerifyRequest_noArgsConstructor_shouldCreateEmpty() {
		MfaVerifyRequest request = new MfaVerifyRequest();

		assertNull(request.getEmail());
		assertNull(request.getCode());
	}

	@Test
	void userResponse_builder_shouldCreateWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		UserResponse response = UserResponse.builder()
				.id(1L)
				.email("user@vendnet.com")
				.name("User Name")
				.role("ROLE_CUSTOMER")
				.createdAt(now)
				.build();

		assertEquals(1L, response.getId());
		assertEquals("user@vendnet.com", response.getEmail());
		assertEquals("User Name", response.getName());
		assertEquals("ROLE_CUSTOMER", response.getRole());
		assertEquals(now, response.getCreatedAt());
	}

	@Test
	void userResponse_setters_shouldModifyFields() {
		UserResponse response = new UserResponse();
		LocalDateTime now = LocalDateTime.now();
		response.setId(2L);
		response.setEmail("admin@vendnet.com");
		response.setName("Admin");
		response.setRole("ROLE_ADMINISTRATOR");
		response.setCreatedAt(now);

		assertEquals(2L, response.getId());
		assertEquals("admin@vendnet.com", response.getEmail());
		assertEquals("Admin", response.getName());
		assertEquals("ROLE_ADMINISTRATOR", response.getRole());
		assertEquals(now, response.getCreatedAt());
	}

	@Test
	void userResponse_noArgsConstructor_shouldCreateEmpty() {
		UserResponse response = new UserResponse();

		assertNull(response.getId());
		assertNull(response.getEmail());
		assertNull(response.getName());
		assertNull(response.getRole());
		assertNull(response.getCreatedAt());
	}

	@Test
	void userResponse_allArgsConstructor_shouldCreate() {
		LocalDateTime now = LocalDateTime.now();
		UserResponse response = new UserResponse(3L, "ops@vendnet.com", "Operator", "ROLE_OPERATOR", now);

		assertEquals(3L, response.getId());
		assertEquals("ops@vendnet.com", response.getEmail());
		assertEquals("Operator", response.getName());
		assertEquals("ROLE_OPERATOR", response.getRole());
		assertEquals(now, response.getCreatedAt());
	}

	@Test
	void claimsResponse_builder_shouldCreateWithAllFields() {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + 86400000);

		ClaimsResponse response = ClaimsResponse.builder()
				.subject("user@vendnet.com")
				.role("ROLE_CUSTOMER")
				.issuedAt(issuedAt)
				.expiration(expiration)
				.build();

		assertEquals("user@vendnet.com", response.getSubject());
		assertEquals("ROLE_CUSTOMER", response.getRole());
		assertEquals(issuedAt, response.getIssuedAt());
		assertEquals(expiration, response.getExpiration());
	}

	@Test
	void claimsResponse_setters_shouldModifyFields() {
		ClaimsResponse response = new ClaimsResponse();
		Date issuedAt = new Date();
		Date expiration = new Date();

		response.setSubject("admin@vendnet.com");
		response.setRole("ROLE_ADMINISTRATOR");
		response.setIssuedAt(issuedAt);
		response.setExpiration(expiration);

		assertEquals("admin@vendnet.com", response.getSubject());
		assertEquals("ROLE_ADMINISTRATOR", response.getRole());
		assertEquals(issuedAt, response.getIssuedAt());
		assertEquals(expiration, response.getExpiration());
	}

	@Test
	void claimsResponse_noArgsConstructor_shouldCreateEmpty() {
		ClaimsResponse response = new ClaimsResponse();

		assertNull(response.getSubject());
		assertNull(response.getRole());
		assertNull(response.getIssuedAt());
		assertNull(response.getExpiration());
	}

	@Test
	void claimsResponse_allArgsConstructor_shouldCreate() {
		Date issuedAt = new Date();
		Date expiration = new Date();
		ClaimsResponse response = new ClaimsResponse("test@vendnet.com", "ROLE_OPERATOR", issuedAt, expiration);

		assertEquals("test@vendnet.com", response.getSubject());
		assertEquals("ROLE_OPERATOR", response.getRole());
		assertEquals(issuedAt, response.getIssuedAt());
		assertEquals(expiration, response.getExpiration());
	}
}
