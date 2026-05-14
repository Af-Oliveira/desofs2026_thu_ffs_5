package pt.isep.desofs.vendnet.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

	String store(MultipartFile file, String directory);

	void delete(String filePath);
}
