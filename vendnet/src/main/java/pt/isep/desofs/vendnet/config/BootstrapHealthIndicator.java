package pt.isep.desofs.vendnet.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("bootstrap")
public class BootstrapHealthIndicator implements HealthIndicator {

	private final BootstrapReadyIndicator readyIndicator;

	public BootstrapHealthIndicator(BootstrapReadyIndicator readyIndicator) {
		this.readyIndicator = readyIndicator;
	}

	@Override
	public Health health() {
		if (readyIndicator.isReady()) {
			return Health.up().withDetail("bootstrap", "complete").build();
		}
		return Health.down().withDetail("bootstrap", "seeding").build();
	}
}
