package pt.isep.desofs.vendnet.application.service;

public class TotpGenerationException extends RuntimeException {

	public TotpGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
}
