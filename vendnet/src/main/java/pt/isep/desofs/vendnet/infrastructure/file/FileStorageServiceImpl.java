package pt.isep.desofs.vendnet.infrastructure.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.infrastructure.os.PathValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

	private final FileValidationService fileValidationService;
	private final PathValidator pathValidator;

	@Value("${app.storage.base-path:/var/vendnet/uploads}")
	private String basePath;

	@Override
	public String store(MultipartFile file, String subDirectory) {
		fileValidationService.validate(file);

		try {
			Path sandbox = Paths.get(basePath).toRealPath();
			Path targetDir = sandbox.resolve(subDirectory);

			if (!pathValidator.isValidPath(targetDir, sandbox)) {
				throw new SecurityException("Path traversal detected: " + targetDir);
			}

			Files.createDirectories(targetDir);

			String originalName = file.getOriginalFilename();
			String extension =
					originalName != null && originalName.contains(".")
							? originalName.substring(originalName.lastIndexOf("."))
							: "";
			String storedName = UUID.randomUUID().toString() + extension;
			Path targetFile = targetDir.resolve(storedName);

			if (pathValidator.containsSymlink(targetFile)) {
				throw new SecurityException("Symlink detected: " + targetFile);
			}

			Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

			String relativePath = Path.of(basePath).relativize(targetFile).toString();
			log.info("File stored: {}", relativePath);

			return relativePath;
		} catch (IOException e) {
			log.error("File storage failed", e);
			throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
		}
	}

	@Override
	public void delete(String filePath) {
		try {
			Path sandbox = Paths.get(basePath).toRealPath();
			Path target = sandbox.resolve(filePath);

			if (!pathValidator.isValidPath(target, sandbox)) {
				throw new SecurityException("Path traversal detected during delete: " + target);
			}

			Files.deleteIfExists(target);
			log.info("File deleted: {}", filePath);
		} catch (IOException e) {
			log.error("File deletion failed: {}", filePath, e);
			throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
		}
	}
}
