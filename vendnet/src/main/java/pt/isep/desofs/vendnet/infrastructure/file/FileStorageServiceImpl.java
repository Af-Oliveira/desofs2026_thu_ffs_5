package pt.isep.desofs.vendnet.infrastructure.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.infrastructure.os.PathValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileValidationService fileValidationService;
    private final PathValidator pathValidator;

    @Override
    public String store(MultipartFile file, String directory) {
        fileValidationService.validate(file);
        log.info("File storage triggered (dir={}, name={}) — not yet implemented", directory, file.getOriginalFilename());
        return directory + "/" + file.getOriginalFilename();
    }

    @Override
    public void delete(String filePath) {
        log.info("File deletion triggered (path={}) — not yet implemented", filePath);
    }
}
