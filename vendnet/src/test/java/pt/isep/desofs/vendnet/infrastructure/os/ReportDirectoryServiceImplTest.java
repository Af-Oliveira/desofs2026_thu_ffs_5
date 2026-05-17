package pt.isep.desofs.vendnet.infrastructure.os;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportDirectoryServiceImplTest {

	@TempDir
	Path tempDir;

	@Mock private PathValidator pathValidator;

	private ReportDirectoryServiceImpl service;

	@BeforeEach
	void setUp() throws IOException {
		service = new ReportDirectoryServiceImpl(pathValidator);
		Path vendnetRoot = tempDir.resolve("vendnet");
		Files.createDirectories(vendnetRoot);
		org.springframework.test.util.ReflectionTestUtils.setField(service, "vendnetRoot", vendnetRoot.toString());
	}

	@Test
	void createReportDirectory_invalidType_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> service.createReportDirectory("invalid"));
	}

	@Test
	void createReportDirectory_pathTraversal_type_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> service.createReportDirectory("../../etc"));
	}

	@Test
	void createReportDirectory_sales_shouldCreateDir() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		String result = service.createReportDirectory("sales");
		assertTrue(result.contains("sales"));
		assertTrue(Files.exists(Path.of(result)));
	}

	@Test
	void createReportDirectory_inventory_shouldCreateDir() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		String result = service.createReportDirectory("inventory");
		assertTrue(result.contains("inventory"));
		assertTrue(Files.exists(Path.of(result)));
	}

	@Test
	void createReportDirectory_idempotent_shouldNotThrow() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		assertDoesNotThrow(() -> {
			service.createReportDirectory("sales");
			service.createReportDirectory("sales");
		});
	}
}