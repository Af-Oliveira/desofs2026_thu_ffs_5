package pt.isep.desofs.vendnet.infrastructure.file;

import java.io.ByteArrayInputStream;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileValidationServiceImpl implements FileValidationService {

	private static final Set<String> ALLOWED_CONTENT_TYPES =
			Set.of("image/jpeg", "image/png", "image/webp");

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

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
		} catch (Exception e) {
			log.warn("Magic byte detection failed", e);
			return false;
		}
	}

	private boolean hasValidMagicBytes(MultipartFile file) {
		try {
			String detected = tika.detect(file.getInputStream());
			return ALLOWED_CONTENT_TYPES.contains(detected);
		} catch (Exception e) {
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
}
