package pt.isep.desofs.vendnet.infrastructure.os;

import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathValidatorImpl implements PathValidator {

	@Override
	public boolean isValidPath(Path path, Path sandboxRoot) {
		try {
			Path realPath = path.toRealPath();
			Path realRoot = sandboxRoot.toRealPath();
			return realPath.startsWith(realRoot);
		} catch (Exception e) {
			log.warn("Path validation failed: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean containsSymlink(Path path) {
		return Files.isSymbolicLink(path);
	}
}
