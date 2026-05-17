package pt.isep.desofs.vendnet.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BootstrapReadyIndicatorTest {

	private BootstrapReadyIndicator indicator;

	@BeforeEach
	void setUp() {
		indicator = new BootstrapReadyIndicator();
	}

	@Test
	void isReady_initially_shouldReturnFalse() {
		assertFalse(indicator.isReady());
	}

	@Test
	void isReady_afterMarkReady_shouldReturnTrue() {
		indicator.markReady();
		assertTrue(indicator.isReady());
	}

	@Test
	void markReady_shouldBeIdempotent() {
		indicator.markReady();
		indicator.markReady();
		assertTrue(indicator.isReady());
	}
}