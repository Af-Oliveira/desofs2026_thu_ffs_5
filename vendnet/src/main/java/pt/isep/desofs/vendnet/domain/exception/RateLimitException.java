package pt.isep.desofs.vendnet.domain.exception;

public class RateLimitException extends RuntimeException {
	public RateLimitException(String message) {
		super(message);
	}
}
