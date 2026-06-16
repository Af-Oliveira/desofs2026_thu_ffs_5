package pt.isep.desofs.vendnet.infrastructure.os;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PathValidatorImplTest {

	private final PathValidatorImpl validator = new PathValidatorImpl();

	@Test
	void isValidPath_childDirectory_shouldReturnTrue(@TempDir Path tempDir) throws IOException {
		Path child = tempDir.resolve("subdir");
		Files.createDirectories(child);
		assertTrue(validator.isValidPath(child, tempDir));
	}

	@Test
	void isValidPath_sameDirectory_shouldReturnTrue(@TempDir Path tempDir) {
		assertTrue(validator.isValidPath(tempDir, tempDir));
	}

	@Test
	void isValidPath_outsideDirectory_shouldReturnFalse(@TempDir Path tempDir) {
		Path outside = Path.of("/etc/passwd");
		assertFalse(validator.isValidPath(outside, tempDir));
	}

	@Test
	void containsSymlink_shouldDetectSymlink(@TempDir Path tempDir) throws IOException {
		Path target = tempDir.resolve("target");
		Files.createDirectory(target);
		Path link = tempDir.resolve("link");
		try {
			Files.createSymbolicLink(link, target);
			assertTrue(validator.containsSymlink(link));
		} catch (UnsupportedOperationException e) {
			// Some filesystems do not support symbolic links in test sandboxes.
		}
	}

	@Test
	void containsSymlink_regularFile_shouldReturnFalse(@TempDir Path tempDir) throws IOException {
		Path regularFile = tempDir.resolve("regular.txt");
		Files.writeString(regularFile, "test");
		assertFalse(validator.containsSymlink(regularFile));
	}
}
