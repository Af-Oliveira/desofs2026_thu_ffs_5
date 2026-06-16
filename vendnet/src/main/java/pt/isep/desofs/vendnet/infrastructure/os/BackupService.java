package pt.isep.desofs.vendnet.infrastructure.os;

public interface BackupService {

	BackupResult generateBackup();

	void rotateBackups(int retentionDays);
}
