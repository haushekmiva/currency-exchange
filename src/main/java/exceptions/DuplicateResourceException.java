package exceptions;

public class DuplicateResourceException extends ApplicationException {
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
