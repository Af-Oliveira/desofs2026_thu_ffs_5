package pt.isep.desofs.vendnet.infrastructure.os;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 128;

	private final PathValidator pathValidator;
	private final AuditLogRepository auditLogRepository;

	@Value("${app.storage.base-path:/var/vendnet}")
	private String vendnetRoot;

	@Override
	public void generateBackup() {
		String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		Path backupDir = Paths.get(vendnetRoot, "backups", date);

		try {
			Path sandbox = Paths.get(vendnetRoot).toRealPath();
			if (!pathValidator.isValidPath(backupDir, sandbox)) {
				auditLogRepository.save(
						AuditLog.builder()
								.eventType("SECURITY_VIOLATION")
								.details("Path traversal attempt: " + backupDir)
								.resource("Backup")
								.action("CREATE")
								.outcome("BLOCKED")
								.timestamp(LocalDateTime.now())
								.build());
				throw new SecurityException("Backup path outside sandbox: " + backupDir);
			}

			Files.createDirectories(backupDir);

			Path dumpFile = backupDir.resolve("vendnet_backup_" + date + ".sql");
			ProcessBuilder pb =
					new ProcessBuilder(
							"mysqldump",
							"-h",
							"localhost",
							"-u",
							"vendnet_user",
							"--password=vendnet_pass",
							"--single-transaction",
							"--routines",
							"--triggers",
							"--databases",
							"vendnet",
							"--result-file=" + dumpFile.toAbsolutePath().toString());
			pb.redirectErrorStream(true);

			Process process = pb.start();
			int exitCode = process.waitFor();

			if (exitCode != 0) {
				String error = new String(process.getInputStream().readAllBytes());
				log.error("mysqldump failed (exit {}): {}", exitCode, error);
				throw new RuntimeException("Backup failed: mysqldump exited with " + exitCode);
			}

			encryptFile(dumpFile);

			auditLogRepository.save(
					AuditLog.builder()
							.eventType("BACKUP_CREATED")
							.details("Backup generated: " + dumpFile.getFileName())
							.resource("Backup")
							.action("CREATE")
							.outcome("SUCCESS")
							.timestamp(LocalDateTime.now())
							.build());

			rotateBackups(30);

			log.info("Backup generated and encrypted: {}", dumpFile);
		} catch (Exception e) {
			log.error("Backup generation failed", e);
			throw new RuntimeException("Backup failed: " + e.getMessage(), e);
		}
	}

	@Override
	public void rotateBackups(int retentionDays) {
		try {
			Path backupsRoot = Paths.get(vendnetRoot, "backups").toRealPath();
			Path sandbox = Paths.get(vendnetRoot).toRealPath();

			if (!pathValidator.isValidPath(backupsRoot, sandbox)) {
				throw new SecurityException("Backup rotation outside sandbox");
			}

			LocalDate cutoff = LocalDate.now().minusDays(retentionDays);

			try (Stream<Path> dirs = Files.list(backupsRoot)) {
				dirs.filter(Files::isDirectory)
						.forEach(
								dir -> {
									try {
										LocalDate dirDate =
												LocalDate.parse(dir.getFileName().toString());
										if (dirDate.isBefore(cutoff)) {
											Files.walk(dir)
													.sorted(Comparator.reverseOrder())
													.forEach(
															f -> {
																try {
																	Files.deleteIfExists(f);
																} catch (Exception ignored) {
																	/* ok */
																}
															});
											log.info(
													"Rotated backup directory: {}",
													dir.getFileName());
										}
									} catch (Exception ignored) {
										/* ok */
									}
								});
			}
		} catch (Exception e) {
			log.error("Backup rotation failed", e);
		}
	}

	private void encryptFile(Path file) throws Exception {
		byte[] fileBytes = Files.readAllBytes(file);
		byte[] iv = new byte[GCM_IV_LENGTH];
		new SecureRandom().nextBytes(iv);

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey key = keyGen.generateKey();
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

		byte[] encrypted = cipher.doFinal(fileBytes);

		byte[] result = new byte[iv.length + encrypted.length];
		System.arraycopy(iv, 0, result, 0, iv.length);
		System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

		Path encryptedFile = Paths.get(file.toString() + ".enc");
		Files.write(encryptedFile, result);
		Files.deleteIfExists(file);

		log.info("File encrypted with AES-256-GCM: {}", encryptedFile.getFileName());
	}
}
