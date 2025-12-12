package extractors;

import exceptions.InputException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * В этом классе есть семантическая ошибка. Один метод выкидывает искл., а другой кидает null.
 * Я понимаю и осознаю косяк, но не знаю как его исправить тк не хочу плодить много схожих классов
 */
public class InputExtractor {

    public static double extractDouble(String string, String argumentName) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new InputException(String.format("Argument %s must be a number.", argumentName));
        }
    }

    // возвращает null, чтобы подстроиться под стандартный .getParameter
    public static String extractArgumentFromInputStream(InputStream inputStream, String argumentName) throws IOException {
        String userArguments = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String[] argumentsSeparated = userArguments.split("&");
        for (String argument : argumentsSeparated) {
            String[] argumentParts = argument.split("=");
            if (argumentParts[0].equals(argumentName)) {
                return argumentParts[1];
            }
        }
        return null;
    }

}
