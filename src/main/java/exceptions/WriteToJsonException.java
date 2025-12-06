package exceptions;

public class WriteToJsonException extends RuntimeException {

    public WriteToJsonException(String message) {
        super(message);
    }

    public WriteToJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteToJsonException() {
        super();
    }
}