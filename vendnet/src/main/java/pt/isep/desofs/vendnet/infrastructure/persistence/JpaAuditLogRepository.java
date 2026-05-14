package pt.isep.desofs.vendnet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;

import java.util.List;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLog, Long>, pt.isep.desofs.vendnet.domain.repository.AuditLogRepository {

    List<AuditLog> findByEventType(String eventType);
}
