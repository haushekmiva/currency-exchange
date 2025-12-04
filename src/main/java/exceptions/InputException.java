package exceptions;

public class InputException extends ApplicationException {
    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException() {
        super();
    }
}
