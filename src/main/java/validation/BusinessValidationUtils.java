package validation;

import exceptions.InputException;

import java.util.regex.Pattern;

public class BusinessValidationUtils {
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    private static final Pattern CURRENCY_FULLANME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]{3,20}$");
    private static final Pattern CURRENCY_SIGN_PATTERN = Pattern.compile("^.{1,5}$");

    public static void validateCurrencyCode(String code) {
        validateHelper(code, CURRENCY_CODE_PATTERN, "Currency code must have 3 symbols in upper-case.");
    }

    public static void validateCurrencyFullName(String fullName) {
        validateHelper(fullName, CURRENCY_FULLANME_PATTERN, "Currency full name must have from 3 to 20 symbols.");

    }

    public static void validateCurrencySign(String sign) {
        validateHelper(sign, CURRENCY_SIGN_PATTERN, "Currency sign must have from 1 to 5 symbols.");
    }

    public static void validateRate(double rate) {
        if (rate < 0.0) {
            throw new InputException("Rate must be more than 0.");
        }
    }

    private static void validateHelper(String string, Pattern pattern, String message) {
        if (!pattern.matcher(string).matches()) {
            throw new InputException(message);
        }
    }

}
