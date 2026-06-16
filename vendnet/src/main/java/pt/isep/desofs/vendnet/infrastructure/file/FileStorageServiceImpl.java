package pt.isep.desofs.vendnet.infrastructure.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pt.isep.desofs.vendnet.domain.exception.FileStorageException;
import pt.isep.desofs.vendnet.infrastructure.os.PathValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

	private final FileValidationService fileValidationService;
	private final PathValidator pathValidator;
	private final ExifStripper exifStripper;

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
			String extension = originalName != null && originalName.contains(".")
					? originalName.substring(originalName.lastIndexOf("."))
					: "";
			String storedName = UUID.randomUUID().toString() + extension;
			Path targetFile = targetDir.resolve(storedName);

			if (pathValidator.containsSymlink(targetFile)) {
				throw new SecurityException("Symlink detected: " + targetFile);
			}

			FileValidationServiceImpl validator = (FileValidationServiceImpl) fileValidationService;
			byte[] originalBytes = file.getBytes();

			String detectedFormat = extension.replace(".", "");
			if ("jpg".equalsIgnoreCase(detectedFormat)) {
				detectedFormat = "jpeg";
			}
			byte[] strippedBytes = exifStripper.stripExif(originalBytes, detectedFormat);

			String checksum = validator.computeChecksum(strippedBytes);
			log.info("Image checksum (SHA-256): {}", checksum);

			Files.write(targetFile, strippedBytes, StandardOpenOption.CREATE_NEW);
			setStoredFilePermissions(targetFile);

			String relativePath = sandbox.relativize(targetFile).toString();
			log.info("File stored: {} (checksum: {})", relativePath, checksum);

			return relativePath;
		} catch (IOException e) {
			log.error("File storage failed", e);
			throw new FileStorageException("Failed to store file: " + e.getMessage(), e);
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
			throw new FileStorageException("Failed to delete file: " + e.getMessage(), e);
		}
	}

	private void setStoredFilePermissions(Path targetFile) throws IOException {
		try {
			Files.setPosixFilePermissions(
					targetFile,
					Set.of(
							PosixFilePermission.OWNER_READ,
							PosixFilePermission.OWNER_WRITE,
							PosixFilePermission.GROUP_READ));
		} catch (UnsupportedOperationException ignored) {
			// Non-POSIX filesystems keep default permissions.
		}
	}
}
