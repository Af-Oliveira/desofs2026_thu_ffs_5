package pt.isep.desofs.vendnet.config;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("bootstrap")
public class BootstrapReadyIndicator {

	private final AtomicBoolean ready = new AtomicBoolean(false);

	public void markReady() {
		ready.set(true);
		log.info("Bootstrap seeding complete — app is ready");
	}

	public boolean isReady() {
		return ready.get();
	}
}
