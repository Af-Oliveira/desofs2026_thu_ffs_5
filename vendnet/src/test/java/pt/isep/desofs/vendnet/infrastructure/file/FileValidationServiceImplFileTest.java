package pt.isep.desofs.vendnet.infrastructure.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

@Tag("unit")
class FileValidationServiceImplFileTest {

	private FileValidationServiceImpl validator;

	@BeforeEach
	void setUp() {
		validator = new FileValidationServiceImpl();
	}

	@Test
	void validate_validPng_shouldNotThrow() throws IOException {
		MockMultipartFile file =
				new MockMultipartFile("image", "photo.png", "image/png", createPngBytes());
		assertDoesNotThrow(() -> validator.validate(file));
	}

	@Test
	void validate_validJpeg_shouldNotThrow() throws IOException {
		MockMultipartFile file =
				new MockMultipartFile("image", "photo.jpg", "image/jpeg", createJpegBytes());
		assertDoesNotThrow(() -> validator.validate(file));
	}

	@Test
	void validate_jpegContentWithPngExtension_shouldThrow() throws IOException {
		MockMultipartFile file =
				new MockMultipartFile("image", "fake.png", "image/png", createJpegBytes());
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_nullFilename_shouldThrow() throws IOException {
		MockMultipartFile file =
				new MockMultipartFile("image", null, "image/png", createPngBytes());
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_truncatedJpeg_shouldThrow() {
		byte[] truncated = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
		MockMultipartFile file =
				new MockMultipartFile("image", "broken.jpg", "image/jpeg", truncated);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_validJpegWithJpegExtension_shouldNotThrow() throws IOException {
		MockMultipartFile file =
				new MockMultipartFile("image", "photo.jpeg", "image/jpeg", createJpegBytes());
		assertDoesNotThrow(() -> validator.validate(file));
	}

	@Test
	void isAllowedContentType_uppercase_shouldReturnTrue() {
		assertTrue(validator.isAllowedContentType("IMAGE/PNG"));
	}

	@Test
	void validate_fileWithInvalidExtension_shouldThrow() {
		byte[] content = "not an image".getBytes();
		MockMultipartFile file =
				new MockMultipartFile("file", "malware.exe", "image/jpeg", content);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_fileWithInvalidContentType_shouldThrow() {
		byte[] content = "not an image".getBytes();
		MockMultipartFile file =
				new MockMultipartFile("file", "test.jpg", "application/pdf", content);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void computeChecksum_shouldReturn64CharHex() {
		String checksum = validator.computeChecksum("test data".getBytes());
		assertEquals(64, checksum.length());
	}

	@Test
	void computeChecksum_sameInput_sameOutput() {
		byte[] data = "consistent".getBytes();
		assertEquals(validator.computeChecksum(data), validator.computeChecksum(data));
	}

	private byte[] createPngBytes() throws IOException {
		return writeImage(createImage(Color.RED), "png");
	}

	private byte[] createJpegBytes() throws IOException {
		return writeImage(createImage(Color.BLUE), "jpeg");
	}

	private BufferedImage createImage(Color color) {
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, 10, 10);
		graphics.dispose();
		return image;
	}

	private byte[] writeImage(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, format, output);
		return output.toByteArray();
	}
}
