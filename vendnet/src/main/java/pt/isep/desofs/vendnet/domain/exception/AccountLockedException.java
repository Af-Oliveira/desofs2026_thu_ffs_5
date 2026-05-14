package pt.isep.desofs.vendnet.domain.exception;

public class AccountLockedException extends RuntimeException {
	public AccountLockedException(String message) {
		super(message);
	}
}
