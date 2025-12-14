package exceptions;

public class ReadFromJsonException extends ApplicationException {

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