package pt.isep.desofs.vendnet.domain.exception;

public class CapacityExceededException extends RuntimeException {
	public CapacityExceededException(String message) {
		super(message);
	}
}
