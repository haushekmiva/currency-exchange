package validation;

import exceptions.InputException;
import models.CurrencyCreationRequest;

import java.util.HashMap;
import java.util.Map;

public class PreconditionValidator {

    public static void validateCreateCurrencyArguments(CurrencyCreationRequest userInput) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put("name", userInput.name());
        arguments.put("code", userInput.code());
        arguments.put("sign", userInput.sign());

        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            String argumentName = entry.getKey();
            String argumentValue = entry.getValue();
            if ((argumentValue == null) || (argumentValue.isBlank())) {
                throw new InputException(String.format("Required argument currency %s was not provided.", argumentName));
            }
        }
    }

    public static void validateGetCurrencyArguments(String currencyCode) {
        if ((currencyCode == null) || (currencyCode.isBlank())) {
            throw new InputException("Required argument currency code was not provided.");
        }
    }

}
