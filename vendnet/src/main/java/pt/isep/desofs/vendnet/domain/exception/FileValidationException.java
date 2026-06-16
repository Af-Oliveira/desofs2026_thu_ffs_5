package pt.isep.desofs.vendnet.domain.exception;

public class FileValidationException extends RuntimeException {

	public FileValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
