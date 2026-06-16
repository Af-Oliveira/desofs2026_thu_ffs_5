package pt.isep.desofs.vendnet.infrastructure.os;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AuditLogRotationServiceImplTest {

	@TempDir Path tempDir;

	@Mock private PathValidator pathValidator;

	private AuditLogRotationServiceImpl service;

	@BeforeEach
	void setUp() throws IOException {
		service = new AuditLogRotationServiceImpl(pathValidator);
		ReflectionTestUtils.setField(service, "vendnetRoot", tempDir.toString());
		ReflectionTestUtils.setField(service, "hmacSecret", UUID.randomUUID().toString());
		Files.createDirectories(tempDir.resolve("logs/audit"));
	}

	@Test
	void rotate_invalidSandbox_shouldNotThrow() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);
		assertDoesNotThrow(() -> service.rotate());
	}

	@Test
	void deleteAfterDays_invalidSandbox_shouldNotThrow() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);
		assertDoesNotThrow(() -> service.deleteAfterDays(7));
	}

	@Test
	void rotate_oldLogFile_shouldCompressAndRemoveOriginal() throws IOException {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		Path logFile = tempDir.resolve("logs/audit/app.log");
		Files.writeString(logFile, "audit entry");
		Files.setLastModifiedTime(logFile, FileTime.from(Instant.now().minus(2, ChronoUnit.DAYS)));

		service.rotate();

		assertFalse(Files.exists(logFile));
		assertTrue(Files.exists(Path.of(logFile + ".gz")));
		assertTrue(Files.exists(Path.of(logFile + ".hmac")));
	}

	@Test
	void rotate_recentLogFile_shouldLeaveUntouched() throws IOException {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		Path logFile = tempDir.resolve("logs/audit/recent.log");
		Files.writeString(logFile, "recent entry");

		service.rotate();

		assertTrue(Files.exists(logFile));
	}

	@Test
	void compressAfterDays_oldLogFile_shouldCompress() throws IOException {
		Path logFile = tempDir.resolve("logs/audit/archive.log");
		Files.writeString(logFile, "archive entry");
		Files.setLastModifiedTime(logFile, FileTime.from(Instant.now().minus(5, ChronoUnit.DAYS)));

		service.compressAfterDays(3);

		assertFalse(Files.exists(logFile));
		assertTrue(Files.exists(Path.of(logFile + ".gz")));
	}

	@Test
	void deleteAfterDays_oldLogFile_shouldRemove() throws IOException {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		Path logFile = tempDir.resolve("logs/audit/old.log");
		Files.writeString(logFile, "old entry");
		Files.setLastModifiedTime(logFile, FileTime.from(Instant.now().minus(10, ChronoUnit.DAYS)));

		service.deleteAfterDays(7);

		assertFalse(Files.exists(logFile));
	}
}
