package pt.isep.desofs.vendnet.domain.model.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AuditLogTest {

	@Test
	void builder_shouldCreateAuditLogWithAllFields() {
		LocalDateTime now = LocalDateTime.now();

		AuditLog log = AuditLog.builder()
				.id(1L)
				.eventType("LOGIN_SUCCESS")
				.principal("user@vendnet.com")
				.details("Login from IP 192.168.1.1")
				.resource("AuthController")
				.action("login")
				.outcome("SUCCESS")
				.ipAddress("192.168.1.1")
				.timestamp(now)
				.integrityHash("sha256abc123")
				.build();

		assertEquals(1L, log.getId());
		assertEquals("LOGIN_SUCCESS", log.getEventType());
		assertEquals("user@vendnet.com", log.getPrincipal());
		assertEquals("Login from IP 192.168.1.1", log.getDetails());
		assertEquals("AuthController", log.getResource());
		assertEquals("login", log.getAction());
		assertEquals("SUCCESS", log.getOutcome());
		assertEquals("192.168.1.1", log.getIpAddress());
		assertEquals(now, log.getTimestamp());
		assertEquals("sha256abc123", log.getIntegrityHash());
	}

	@Test
	void setters_shouldModifyFields() {
		AuditLog log = new AuditLog();
		LocalDateTime now = LocalDateTime.now();

		log.setId(2L);
		log.setEventType("LOGIN_FAILED");
		log.setPrincipal("attacker@vendnet.com");
		log.setDetails("Failed login attempt");
		log.setResource("AuthController");
		log.setAction("login");
		log.setOutcome("FAILURE");
		log.setIpAddress("10.0.0.1");
		log.setTimestamp(now);
		log.setIntegrityHash("sha256def456");

		assertEquals(2L, log.getId());
		assertEquals("LOGIN_FAILED", log.getEventType());
		assertEquals("attacker@vendnet.com", log.getPrincipal());
		assertEquals("Failed login attempt", log.getDetails());
		assertEquals("AuthController", log.getResource());
		assertEquals("login", log.getAction());
		assertEquals("FAILURE", log.getOutcome());
		assertEquals("10.0.0.1", log.getIpAddress());
		assertEquals(now, log.getTimestamp());
		assertEquals("sha256def456", log.getIntegrityHash());
	}

	@Test
	void noArgsConstructor_shouldCreateEmptyAuditLog() {
		AuditLog log = new AuditLog();

		assertNull(log.getId());
		assertNull(log.getEventType());
		assertNull(log.getPrincipal());
		assertNull(log.getDetails());
		assertNull(log.getResource());
		assertNull(log.getAction());
		assertNull(log.getOutcome());
		assertNull(log.getIpAddress());
		assertNull(log.getTimestamp());
		assertNull(log.getIntegrityHash());
	}

	@Test
	void allArgsConstructor_shouldCreateAuditLog() {
		LocalDateTime now = LocalDateTime.now();

		AuditLog log = new AuditLog(
				3L, "ACCESS_GRANTED", "admin@vendnet.com",
				"Accessed admin dashboard", "AdminController",
				"dashboard", "SUCCESS", "172.16.0.1", now, "sha256ghi789");

		assertEquals(3L, log.getId());
		assertEquals("ACCESS_GRANTED", log.getEventType());
		assertEquals("admin@vendnet.com", log.getPrincipal());
		assertEquals("Accessed admin dashboard", log.getDetails());
		assertEquals("AdminController", log.getResource());
		assertEquals("dashboard", log.getAction());
		assertEquals("SUCCESS", log.getOutcome());
		assertEquals("172.16.0.1", log.getIpAddress());
		assertEquals(now, log.getTimestamp());
		assertEquals("sha256ghi789", log.getIntegrityHash());
	}
}
