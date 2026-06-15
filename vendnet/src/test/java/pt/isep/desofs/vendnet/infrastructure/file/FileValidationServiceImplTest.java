package pt.isep.desofs.vendnet.infrastructure.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileValidationServiceImplTest {

	private FileValidationServiceImpl validator;

	@BeforeEach
	void setUp() {
		validator = new FileValidationServiceImpl();
	}

	@Test
	void validate_nullFile_shouldThrow() {
		assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
	}

	@Test
	void validate_emptyFile_shouldThrow() {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_fileTooLarge_shouldThrow() {
		byte[] largeContent = new byte[6 * 1024 * 1024];
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", largeContent);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_invalidContentType_shouldThrow() {
		MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream",
				"data".getBytes());
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_invalidExtension_shouldThrow() {
		MockMultipartFile file = new MockMultipartFile("file", "test.exe", "image/jpeg", "data".getBytes());
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void isAllowedContentType_jpeg_shouldReturnTrue() {
		assertTrue(validator.isAllowedContentType("image/jpeg"));
	}

	@Test
	void isAllowedContentType_png_shouldReturnTrue() {
		assertTrue(validator.isAllowedContentType("image/png"));
	}

	@Test
	void isAllowedContentType_webp_shouldReturnTrue() {
		assertTrue(validator.isAllowedContentType("image/webp"));
	}

	@Test
	void isAllowedContentType_null_shouldReturnFalse() {
		assertFalse(validator.isAllowedContentType(null));
	}

	@Test
	void isAllowedContentType_pdf_shouldReturnFalse() {
		assertFalse(validator.isAllowedContentType("application/pdf"));
	}

	@Test
	void hasValidMagicBytes_jpeg_shouldReturnTrue() {
		byte[] jpegBytes = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10 };
		assertTrue(validator.hasValidMagicBytes(jpegBytes));
	}

	@Test
	void hasValidMagicBytes_invalid_shouldReturnFalse() {
		byte[] invalid = "not an image".getBytes();
		assertFalse(validator.hasValidMagicBytes(invalid));
	}

	@Test
	void computeChecksum_shouldReturnHexSha256() {
		byte[] data = "hello world".getBytes();
		String checksum = validator.computeChecksum(data);
		assertEquals(64, checksum.length());
		assertTrue(checksum.matches("[0-9a-f]+"));
	}

	@Test
	void computeChecksum_shouldBeDeterministic() {
		byte[] data = "hello world".getBytes();
		String checksum1 = validator.computeChecksum(data);
		String checksum2 = validator.computeChecksum(data);
		assertEquals(checksum1, checksum2);
	}
}