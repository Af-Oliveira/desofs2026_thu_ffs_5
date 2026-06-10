package pt.isep.desofs.vendnet.infrastructure.file;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import pt.isep.desofs.vendnet.infrastructure.os.PathValidator;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

	@TempDir
	Path tempDir;

	@Mock
	private PathValidator pathValidator;

	private FileStorageServiceImpl service;

	@BeforeEach
	void setUp() throws IOException {
		service =
				new FileStorageServiceImpl(
						new FileValidationServiceImpl(), pathValidator, new ExifStripper());
		Files.createDirectories(tempDir);
		ReflectionTestUtils.setField(service, "basePath", tempDir.toString());
	}

	@Test
	void store_validPng_shouldPersistFileAndReturnRelativePath() throws IOException {
		allowStorage();

		String relativePath = service.store(validPngFile(), "products");

		assertTrue(relativePath.startsWith("products"));
		assertTrue(relativePath.endsWith(".png"));
		assertTrue(Files.exists(tempDir.resolve(relativePath)));
		assertTrue(Files.size(tempDir.resolve(relativePath)) > 0);
	}

	@Test
	void store_jpgExtension_shouldStoreStrippedJpeg() throws IOException {
		allowStorage();

		String relativePath = service.store(validJpgFile(), "products");

		assertTrue(relativePath.endsWith(".jpg"));
		assertTrue(Files.exists(tempDir.resolve(relativePath)));
	}

	@Test
	void store_invalidFile_shouldRejectBeforeWriting() {
		MockMultipartFile file =
				new MockMultipartFile("file", "bad.exe", "application/octet-stream", "x".getBytes());

		assertThrows(IllegalArgumentException.class, () -> service.store(file, "products"));
		assertFalse(Files.exists(tempDir.resolve("products")));
	}

	@Test
	void store_pathTraversal_shouldThrowSecurityException() throws IOException {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);

		assertThrows(
				SecurityException.class, () -> service.store(validPngFile(), "../../etc"));
	}

	@Test
	void store_symlinkTarget_shouldThrowSecurityException() throws IOException {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		when(pathValidator.containsSymlink(any())).thenReturn(true);

		assertThrows(
				SecurityException.class, () -> service.store(validPngFile(), "products"));
	}

	@Test
	void delete_existingFile_shouldRemoveFromDisk() throws IOException {
		allowStorage();
		String relativePath = service.store(validPngFile(), "products");

		service.delete(relativePath);

		assertFalse(Files.exists(tempDir.resolve(relativePath)));
	}

	@Test
	void delete_pathTraversal_shouldThrowSecurityException() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(false);

		assertThrows(SecurityException.class, () -> service.delete("../../etc/passwd"));
	}

	private void allowStorage() {
		when(pathValidator.isValidPath(any(), any())).thenReturn(true);
		when(pathValidator.containsSymlink(any())).thenReturn(false);
	}

	private MockMultipartFile validPngFile() throws IOException {
		return new MockMultipartFile("image", "product.png", "image/png", createPngBytes());
	}

	private MockMultipartFile validJpgFile() throws IOException {
		return new MockMultipartFile("image", "product.jpg", "image/jpeg", createJpegBytes());
	}

	private byte[] createPngBytes() throws IOException {
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.RED);
		graphics.fillRect(0, 0, 10, 10);
		graphics.dispose();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		return output.toByteArray();
	}

	private byte[] createJpegBytes() throws IOException {
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.BLUE);
		graphics.fillRect(0, 0, 10, 10);
		graphics.dispose();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", output);
		return output.toByteArray();
	}
}
