package pt.isep.desofs.vendnet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.isep.desofs.vendnet.application.service.BootstrapService;

@Slf4j
@Configuration
@Profile("bootstrap")
@RequiredArgsConstructor
public class BootstrapProfileConfig implements CommandLineRunner {

	private final BootstrapService bootstrapService;

	@Override
	public void run(String... args) {
		log.info("Bootstrap profile activated — seeding database...");
		bootstrapService.seed();
	}
}
