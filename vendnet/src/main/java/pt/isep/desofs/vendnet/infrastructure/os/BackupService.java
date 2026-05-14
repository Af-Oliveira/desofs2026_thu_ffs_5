package pt.isep.desofs.vendnet.infrastructure.os;

public interface BackupService {

	void generateBackup();

	void rotateBackups(int retentionDays);
}
