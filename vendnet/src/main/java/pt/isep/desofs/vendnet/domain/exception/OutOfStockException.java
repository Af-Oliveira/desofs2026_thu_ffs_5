package pt.isep.desofs.vendnet.domain.exception;

public class OutOfStockException extends RuntimeException {

	public OutOfStockException(String message) {
		super(message);
	}
}
