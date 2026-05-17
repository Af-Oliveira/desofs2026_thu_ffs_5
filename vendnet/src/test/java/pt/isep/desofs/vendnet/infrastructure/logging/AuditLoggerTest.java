package pt.isep.desofs.vendnet.infrastructure.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AuditLoggerTest {

	private final AuditLogger auditLogger = new AuditLogger();

	@Test
	void log_shouldNotThrow() {
		assertDoesNotThrow(() -> auditLogger.log("LOGIN", "admin@vendnet.io", "AUTHENTICATE", "/api/auth/login", "SUCCESS", "User logged in"));
	}

	@Test
	void log_withNullDetails_shouldNotThrow() {
		assertDoesNotThrow(() -> auditLogger.log("LOGIN", "admin@vendnet.io", "AUTHENTICATE", "/api/auth/login", "SUCCESS", null));
	}

	@Test
	void log_withEmptyStrings_shouldNotThrow() {
		assertDoesNotThrow(() -> auditLogger.log("", "", "", "", "", ""));
	}
}