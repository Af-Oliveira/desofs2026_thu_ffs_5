package pt.isep.desofs.vendnet.infrastructure.os;

import java.nio.file.Path;

public interface PathValidator {

	boolean isValidPath(Path path, Path sandboxRoot);

	boolean containsSymlink(Path path);
}
