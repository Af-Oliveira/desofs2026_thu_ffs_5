package pt.isep.desofs.vendnet.infrastructure.os;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogRotationServiceImpl implements AuditLogRotationService {

    private final PathValidator pathValidator;

    @Override
    public void rotate() {
        log.info("Audit log rotation triggered — not yet implemented");
    }

    @Override
    public void compressAfterDays(int days) {
        log.info("Audit log compression triggered ({} days) — not yet implemented", days);
    }

    @Override
    public void deleteAfterDays(int days) {
        log.info("Audit log deletion triggered ({} days) — not yet implemented", days);
    }
}
