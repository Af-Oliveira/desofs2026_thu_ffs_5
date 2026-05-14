package pt.isep.desofs.vendnet.domain.repository;

import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByEventType(String eventType);
}
