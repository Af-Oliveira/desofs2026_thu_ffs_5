package pt.isep.desofs.vendnet.infrastructure.os;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pt.isep.desofs.vendnet.domain.model.audit.AuditLog;
import pt.isep.desofs.vendnet.domain.repository.AuditLogRepository;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class BackupServiceImplTest {

	@TempDir
	Path tempDir;

	@Mock
	private PathValidator pathValidator;

	@Mock
	private AuditLogRepository auditLogRepository;

	private BackupServiceImpl service;
	private DataSource dataSource;

	@BeforeEach
	void setUp() {
		JdbcDataSource jdbcDataSource = new JdbcDataSource();
		jdbcDataSource.setURL("jdbc:h2:mem:backup_test;DB_CLOSE_DELAY=-1");
		jdbcDataSource.setUser("sa");
		jdbcDataSource.setPassword("");
		dataSource = jdbcDataSource;

		service = new BackupServiceImpl(pathValidator, auditLogRepository, dataSource);
		ReflectionTestUtils.setField(service, "vendnetRoot", tempDir.toString());
	}

	@Test
	void generateBackup_validSandbox_shouldCreateEncryptedBackup() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		BackupResult result = service.generateBackup();

		assertNotNull(result.getFilename());
		assertNotNull(result.getChecksum());
		assertTrue(result.getSize() > 0);
	}

	@Test
	void generateBackup_pathOutsideSandbox_shouldReject() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);
		when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

		RuntimeException error =
				assertThrows(RuntimeException.class, () -> service.generateBackup());
		assertTrue(error.getCause() instanceof SecurityException);
	}

	@Test
	void rotateBackups_pathOutsideSandbox_shouldNotDelete() throws Exception {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);
		Path backupDir = tempDir.resolve("backups/2020-01-01");
		Files.createDirectories(backupDir);
		Files.writeString(backupDir.resolve("vendnet_backup_2020-01-01.sql.enc"), "old");

		service.rotateBackups(30);

		assertTrue(Files.exists(backupDir));
	}

	@Test
	void rotateBackups_invalidDirectoryName_shouldSkip() throws Exception {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		Path invalidDir = tempDir.resolve("backups/not-a-date");
		Files.createDirectories(invalidDir);
		Files.writeString(invalidDir.resolve("orphan.sql.enc"), "data");

		service.rotateBackups(30);

		assertTrue(Files.exists(invalidDir));
	}

	@Test
	void rotateBackups_oldDirectory_shouldDeleteExpiredBackup() throws Exception {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		Path expiredDir = tempDir.resolve("backups/2020-01-01");
		Files.createDirectories(expiredDir);
		Files.writeString(expiredDir.resolve("vendnet_backup_2020-01-01.sql.enc"), "old");

		service.rotateBackups(30);

		assertFalse(Files.exists(expiredDir));
	}
}
