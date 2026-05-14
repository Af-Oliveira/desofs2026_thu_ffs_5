package pt.isep.desofs.vendnet.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExceptionTest {

	@Test
	void accountLockedException_shouldStoreMessage() {
		AccountLockedException ex = new AccountLockedException("Account is locked for 30 minutes");

		assertEquals("Account is locked for 30 minutes", ex.getMessage());
		assertTrue(ex instanceof RuntimeException);
	}

	@Test
	void disabledException_shouldStoreMessage() {
		DisabledException ex = new DisabledException("Account is suspended");

		assertEquals("Account is suspended", ex.getMessage());
		assertTrue(ex instanceof RuntimeException);
	}

	@Test
	void unauthorizedException_shouldStoreMessage() {
		UnauthorizedException ex = new UnauthorizedException("Invalid email or password");

		assertEquals("Invalid email or password", ex.getMessage());
		assertTrue(ex instanceof RuntimeException);
	}
}
