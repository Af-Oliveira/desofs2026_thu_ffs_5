package pt.isep.desofs.vendnet.domain.exception;

public class DisabledException extends RuntimeException {
    public DisabledException(String message) {
        super(message);
    }
}
