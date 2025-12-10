package extractors;

import exceptions.InputException;

public class InputExtractor {

    public static double extractDouble(String string, String argumentName) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new InputException(String.format("Argument %s must be a number.", argumentName));
        }
    }

}
