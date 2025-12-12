package service;

import dao.CurrencyDao;
import exceptions.ResourceNotFoundException;
import models.Currency;

import java.util.List;
import java.util.Optional;

import static validation.BusinessValidationUtils.*;


public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.getAll();
    }

    public Currency getCurrencyByCode(String code) {

        validateCurrencyCode(code);
        Optional<Currency> currency = currencyDao.getByCode(code);
        return currency
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Currency with code %s not found.", code)));
    }

    public Currency addCurrency(String code, String fullName, String sign) {

        validateCurrencyCode(code);
        validateCurrencyFullName(fullName);
        validateCurrencySign(sign);

        Currency currency = currencyDao.add(code, fullName, sign);
        return currency;
    }

    private boolean isNotValid(String string, String regex) {
        return !string.matches(regex);
    }

}
