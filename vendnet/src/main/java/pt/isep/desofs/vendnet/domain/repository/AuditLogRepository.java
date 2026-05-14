package pt.isep.desofs.vendnet.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;

public interface AuditLogRepository {
	AuditLog save(AuditLog auditLog);

	List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

	List<AuditLog> findByEventType(String eventType);
}
