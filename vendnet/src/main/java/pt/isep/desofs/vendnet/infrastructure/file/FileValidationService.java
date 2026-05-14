package pt.isep.desofs.vendnet.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {

	void validate(MultipartFile file);

	boolean isAllowedContentType(String contentType);

	boolean hasValidMagicBytes(byte[] data);
}
