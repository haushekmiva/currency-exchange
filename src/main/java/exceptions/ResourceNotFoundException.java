package exceptions;

public class ResourceNotFoundException extends ApplicationException {

    /**
     * Конструктор для создания исключения с заданным сообщением.
     * @param message Подробное сообщение об ошибке (например, "Продукт с ID 123 не найден").
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Конструктор для создания исключения с сообщением и корневой причиной.
     * Обычно используется, когда вы перехватываете другое (низкоуровневое) исключение
     * и оборачиваете его в это прикладное исключение.
     * @param message Подробное сообщение об ошибке.
     * @param cause Корневое исключение, вызвавшее данное исключение.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Конструктор по умолчанию.
     */
    public ResourceNotFoundException() {
        super();
    }

    // Переопределения getMessage() и getCause() обычно не требуются,
    // так как они уже корректно реализованы в RuntimeException и Throwable,
    // но добавлены для соответствия структуре вашего примера.

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}