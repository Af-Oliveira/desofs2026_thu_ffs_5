package pt.isep.desofs.vendnet.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

class BootstrapHealthIndicatorTest {

	private BootstrapReadyIndicator readyIndicator;
	private BootstrapHealthIndicator healthIndicator;

	@BeforeEach
	void setUp() {
		readyIndicator = new BootstrapReadyIndicator();
		healthIndicator = new BootstrapHealthIndicator(readyIndicator);
	}

	@Test
	void health_whenReady_shouldReturnUp() {
		readyIndicator.markReady();
		assertEquals(Status.UP, healthIndicator.health().getStatus());
	}

	@Test
	void health_whenNotReady_shouldReturnDown() {
		assertEquals(Status.DOWN, healthIndicator.health().getStatus());
	}
}