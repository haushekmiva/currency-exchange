package extractors;

import exceptions.InputException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class InputExtractor {

    public static double extractDouble(String string, String argumentName) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new InputException(String.format("Argument %s must be a number.", argumentName));
        }
    }

    // name=RUB&code=RUB&sign=R&rate=7
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
