package pt.isep.desofs.vendnet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import pt.isep.desofs.vendnet.application.service.BootstrapService;

@Slf4j
@Configuration
@Profile("bootstrap")
@RequiredArgsConstructor
public class BootstrapProfileConfig implements CommandLineRunner {

	private final BootstrapService bootstrapService;
	private final BootstrapReadyIndicator readyIndicator;
	private final JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... args) {
		log.info("Bootstrap profile activated — seeding database...");
		repairLegacySlotVersions();
		bootstrapService.seed();
		readyIndicator.markReady();
	}

	private void repairLegacySlotVersions() {
		try {
			int repaired = jdbcTemplate.update("UPDATE slots SET version = 0 WHERE version IS NULL");
			if (repaired > 0) {
				log.info("Repaired {} legacy slot version values", repaired);
			}
		} catch (RuntimeException e) {
			log.debug("Skipping legacy slot version repair: {}", e.getMessage());
		}
	}
}
