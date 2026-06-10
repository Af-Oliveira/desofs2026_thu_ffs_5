package pt.isep.desofs.vendnet.domain.exception;

public class ForbiddenOperationException extends RuntimeException {
	public ForbiddenOperationException(String message) {
		super(message);
	}
}
