package pt.isep.desofs.vendnet.infrastructure.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.domain.exception.FileValidationException;

@Slf4j
@Component
public class FileValidationServiceImpl implements FileValidationService {

	private static final Set<String> ALLOWED_CONTENT_TYPES =
			Set.of("image/jpeg", "image/png", "image/webp");

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

	private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

	private final Tika tika = new Tika();

	@Override
	public void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File exceeds maximum size of 5MB");
		}
		if (!isAllowedContentType(file.getContentType())) {
			throw new IllegalArgumentException("File type not allowed: " + file.getContentType());
		}
		if (!hasAllowedExtension(file.getOriginalFilename())) {
			throw new IllegalArgumentException(
					"File extension not allowed: " + file.getOriginalFilename());
		}
		if (!hasValidMagicBytes(file)) {
			throw new IllegalArgumentException("File content does not match declared MIME type");
		}
		if (!crossCheckMagicBytesWithExtension(file)) {
			throw new IllegalArgumentException(
					"File content does not match file extension. Possible disguised file.");
		}
		if (!isDecodableImage(file)) {
			throw new IllegalArgumentException("File is corrupt or not a valid image");
		}
	}

	@Override
	public boolean isAllowedContentType(String contentType) {
		return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
	}

	@Override
	public boolean hasValidMagicBytes(byte[] data) {
		try {
			String detected = tika.detect(new ByteArrayInputStream(data));
			return ALLOWED_CONTENT_TYPES.contains(detected);
		} catch (IOException e) {
			log.warn("Magic byte detection failed", e);
			return false;
		}
	}

	private boolean hasValidMagicBytes(MultipartFile file) {
		try {
			String detected = tika.detect(file.getInputStream());
			return ALLOWED_CONTENT_TYPES.contains(detected);
		} catch (IOException e) {
			log.warn("Magic byte detection failed for file: {}", file.getOriginalFilename(), e);
			return false;
		}
	}

	private boolean hasAllowedExtension(String filename) {
		if (filename == null) {
			return false;
		}
		String lower = filename.toLowerCase();
		return ALLOWED_EXTENSIONS.stream().anyMatch(lower::endsWith);
	}

	private boolean crossCheckMagicBytesWithExtension(MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
			byte[] firstBytes = java.util.Arrays.copyOf(bytes, Math.min(bytes.length, 16));

			String magicMime = tika.detect(new ByteArrayInputStream(firstBytes));
			String ext = file.getOriginalFilename();
			if (ext == null) {
				return false;
			}
			ext = ext.toLowerCase();

			if (ext.endsWith(".jpg") || ext.endsWith(".jpeg")) {
				return "image/jpeg".equals(magicMime);
			} else if (ext.endsWith(".png")) {
				return "image/png".equals(magicMime);
			} else if (ext.endsWith(".webp")) {
				return "image/webp".equals(magicMime);
			}
			return false;
		} catch (IOException e) {
			log.warn("Cross-check failed", e);
			return false;
		}
	}

	private boolean isDecodableImage(MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			return image != null;
		} catch (IOException e) {
			log.warn("Image decode check failed", e);
			return false;
		}
	}

	public String computeChecksum(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data);
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new FileValidationException("SHA-256 not available", e);
		}
	}
}
