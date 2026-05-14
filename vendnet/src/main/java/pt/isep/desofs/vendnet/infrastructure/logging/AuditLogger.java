package pt.isep.desofs.vendnet.infrastructure.logging;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogger {

	public void log(
			String eventType,
			String principal,
			String action,
			String resource,
			String outcome,
			String details) {
		Map<String, Object> entry = new HashMap<>();
		entry.put("eventType", eventType);
		entry.put("principal", principal);
		entry.put("action", action);
		entry.put("resource", resource);
		entry.put("outcome", outcome);
		entry.put("details", details);
		entry.put("timestamp", LocalDateTime.now().toString());

		log.info("AUDIT {}", entry);
	}
}
