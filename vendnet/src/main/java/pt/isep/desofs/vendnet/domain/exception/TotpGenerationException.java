package pt.isep.desofs.vendnet.domain.exception;

public class TotpGenerationException extends RuntimeException {

	public TotpGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
}
