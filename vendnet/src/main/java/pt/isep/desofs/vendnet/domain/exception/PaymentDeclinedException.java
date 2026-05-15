package pt.isep.desofs.vendnet.domain.exception;

public class PaymentDeclinedException extends RuntimeException {

	public PaymentDeclinedException(String message) {
		super(message);
	}
}
