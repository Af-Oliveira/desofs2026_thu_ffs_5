package pt.isep.desofs.vendnet.infrastructure.os;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final PathValidator pathValidator;

    @Override
    public void generateBackup() {
        log.info("Backup generation triggered — not yet implemented");
    }

    @Override
    public void rotateBackups(int retentionDays) {
        log.info("Backup rotation triggered ({} days) — not yet implemented", retentionDays);
    }
}
