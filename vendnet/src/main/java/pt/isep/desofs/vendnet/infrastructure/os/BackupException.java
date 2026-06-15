package pt.isep.desofs.vendnet.infrastructure.os;

public class BackupException extends RuntimeException {

	public BackupException(String message) {
		super(message);
	}

	public BackupException(String message, Throwable cause) {
		super(message, cause);
	}
}
