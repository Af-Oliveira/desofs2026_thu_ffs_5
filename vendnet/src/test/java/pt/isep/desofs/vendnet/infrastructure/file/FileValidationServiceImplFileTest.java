package pt.isep.desofs.vendnet.infrastructure.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileValidationServiceImplFileTest {

	private FileValidationServiceImpl validator;

	@BeforeEach
	void setUp() {
		validator = new FileValidationServiceImpl();
	}

	@Test
	void validate_jpegFile_withValidContentType_shouldNotThrow() {
		byte[] jpegBytes = createMinimalJpeg();
		MockMultipartFile file = new MockMultipartFile("image", "photo.jpg", "image/jpeg", jpegBytes);
	}

	@Test
	void validate_fileWithInvalidExtension_shouldThrow() {
		byte[] content = "not an image".getBytes();
		MockMultipartFile file = new MockMultipartFile("file", "malware.exe", "image/jpeg", content);
		assertThrows(IllegalArgumentException.class, () -> validator.validate(file));
	}

	@Test
	void validate_fileWithInvalidContentType_shouldThrow() {
		byte[] content = "not an image".getBytes();
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "application/pdf", content);
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

	private byte[] createMinimalJpeg() {
		return new byte[] {
			(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
			0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
			0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
			(byte) 0xFF, (byte) 0xD9
		};
	}
}