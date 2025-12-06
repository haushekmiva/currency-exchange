package exceptions;

public class ReadFromJsonException extends RuntimeException {

    public ReadFromJsonException(String message) {
        super(message);
    }

    public ReadFromJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadFromJsonException() {
        super();
    }
}