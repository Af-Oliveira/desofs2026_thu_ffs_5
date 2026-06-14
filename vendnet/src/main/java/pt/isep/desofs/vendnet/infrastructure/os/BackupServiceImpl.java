package pt.isep.desofs.vendnet.infrastructure.os;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Set;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {

	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 128;

	private final PathValidator pathValidator;
	private final AuditLogRepository auditLogRepository;
	private final DataSource dataSource;
	private final SecretKey backupKey;

	@Value("${app.storage.base-path:/var/vendnet}")
	private String vendnetRoot;

	@Value("${spring.datasource.username:vendnet_user}")
	private String databaseUsername;

	@Value("${spring.datasource.password:vendnet_pass}")
	private String databasePassword;

	@Override
	public BackupResult generateBackup() {
		String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		Path backupDir = Paths.get(vendnetRoot, "backups", date);

		try {
			Path root = Paths.get(vendnetRoot);
			Files.createDirectories(root);
			Path sandbox = root.toRealPath();
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
				setPermissions(
						backupDir,
						Set.of(
								PosixFilePermission.OWNER_READ,
								PosixFilePermission.OWNER_WRITE,
								PosixFilePermission.OWNER_EXECUTE));

				Path dumpFile = backupDir.resolve("vendnet_backup_" + date + ".sql");
				createDatabaseDump(dumpFile);

				Path encryptedFile = encryptFile(dumpFile);
				String checksum = sha256(encryptedFile);
				long size = Files.size(encryptedFile);

			auditLogRepository.save(
					AuditLog.builder()
							.eventType("BACKUP_CREATED")
								.details(
										"Backup generated: "
												+ encryptedFile.getFileName()
												+ ", checksum="
												+ checksum)
							.resource("Backup")
							.action("CREATE")
							.outcome("SUCCESS")
							.timestamp(LocalDateTime.now())
							.build());

				rotateBackups(30);

				log.info("Backup generated and encrypted: {}", encryptedFile);
				return BackupResult.builder()
						.filename(encryptedFile.getFileName().toString())
						.size(size)
						.checksum(checksum)
						.timestamp(LocalDateTime.now())
						.build();
			} catch (Exception e) {
			log.error("Backup generation failed", e);
			throw new RuntimeException("Backup failed: " + e.getMessage(), e);
		}
	}

	public BackupServiceImpl(
			PathValidator pathValidator, AuditLogRepository auditLogRepository, DataSource dataSource) {
		this.pathValidator = pathValidator;
		this.auditLogRepository = auditLogRepository;
		this.dataSource = dataSource;
		this.backupKey = createBackupKey();
	}

	private SecretKey createBackupKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			return keyGen.generateKey();
		} catch (Exception e) {
			throw new IllegalStateException("AES-256 backup key initialization failed", e);
		}
	}

	private void createDatabaseDump(Path dumpFile) throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			String jdbcUrl = connection.getMetaData().getURL();
			if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:h2:")) {
				String escapedPath =
						dumpFile.toAbsolutePath().normalize().toString().replace("'", "''");
				connection.createStatement().execute("SCRIPT TO '" + escapedPath + "'");
				return;
			}
		}

		String jdbcUrl;
		try (Connection connection = dataSource.getConnection()) {
			jdbcUrl = connection.getMetaData().getURL();
		}
		MysqlTarget target = MysqlTarget.fromJdbcUrl(jdbcUrl);
		ProcessBuilder pb =
				new ProcessBuilder(
						"mysqldump",
						"-h",
						target.host(),
						"-P",
						target.port(),
						"-u",
						databaseUsername,
						"--single-transaction",
						"--routines",
						"--triggers",
						"--databases",
						target.database(),
						"--result-file=" + dumpFile.toAbsolutePath().toString());
		pb.environment().put("MYSQL_PWD", databasePassword);
		pb.redirectErrorStream(true);

		Process process = pb.start();
		int exitCode = process.waitFor();

		if (exitCode != 0) {
			String error = new String(process.getInputStream().readAllBytes());
			log.error("mysqldump failed (exit {}): {}", exitCode, error);
			throw new RuntimeException("Backup failed: mysqldump exited with " + exitCode);
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

	private Path encryptFile(Path file) throws Exception {
		byte[] fileBytes = Files.readAllBytes(file);
		byte[] iv = new byte[GCM_IV_LENGTH];
		new SecureRandom().nextBytes(iv);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, backupKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

		byte[] encrypted = cipher.doFinal(fileBytes);

		byte[] result = new byte[iv.length + encrypted.length];
		System.arraycopy(iv, 0, result, 0, iv.length);
		System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

		Path encryptedFile = Paths.get(file.toString() + ".enc");
		Files.write(encryptedFile, result);
			setPermissions(
					encryptedFile,
					Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
		Files.deleteIfExists(file);

		log.info("File encrypted with AES-256-GCM: {}", encryptedFile.getFileName());
		return encryptedFile;
	}

	private String sha256(Path file) throws Exception {
		return HexFormat.of()
				.formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file)));
	}

	private void setPermissions(Path path, Set<PosixFilePermission> permissions) throws Exception {
		try {
			Files.setPosixFilePermissions(path, permissions);
		} catch (UnsupportedOperationException ignored) {
			// Non-POSIX filesystems keep default permissions.
		}
	}

	private record MysqlTarget(String host, String port, String database) {
		private static MysqlTarget fromJdbcUrl(String jdbcUrl) {
			if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql:")) {
				return new MysqlTarget("localhost", "3306", "vendnet");
			}
			try {
				URI uri = URI.create(jdbcUrl.substring("jdbc:".length()));
				String database =
						uri.getPath() == null || uri.getPath().isBlank()
								? "vendnet"
								: uri.getPath().replaceFirst("^/", "");
				return new MysqlTarget(
						uri.getHost() == null ? "localhost" : uri.getHost(),
						uri.getPort() < 0 ? "3306" : String.valueOf(uri.getPort()),
						database);
			} catch (IllegalArgumentException ex) {
				return new MysqlTarget("localhost", "3306", "vendnet");
			}
		}
	}
}
