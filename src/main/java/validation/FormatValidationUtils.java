package validation;

import exceptions.InputException;

public class FormatValidationUtils {

    public static void checkNotEmpty(String argument, String argumentName) {
        if ((argument == null) || (argument.isBlank())) {
            throw new InputException(String.format("Required argument currency %s was not provided.", argumentName));
        }
    }
}
