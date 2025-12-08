package service;

import dao.CurrencyDao;
import exceptions.InputException;
import exceptions.ResourceNotFoundException;
import models.Currency;

import java.util.List;
import java.util.Optional;


public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.getAll();
    }

    public Currency getCurrencyByCode(String code) {
        Optional<Currency> currency = currencyDao.getByCode(code);
        return currency
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Currency with code %s not found.", code)));
    }

    public Currency addCurrency(String code, String fullName, String sign) {

        //------временная заглушка, неоптимизированная валидация------
        if (isNotValid(code, "^[A-Z]{3}$")) {
            throw new InputException("Currency code must have 3 symbols.");
        }

        if (isNotValid(fullName, "^[a-zA-Z0-9\s]{3,20}$")) {
            throw new InputException("Currency full name must have from 3 to 20 symbols.");
        }

        if (isNotValid(sign, "^.{1,5}$")) {
            throw new InputException("Currency sign must have from 1 to 5 symbols.");
        }
        //-----------------------------------------------------------

        Currency currency = currencyDao.add(code, fullName, sign);
        return currency;
    }

    private boolean isNotValid(String string, String regex) {
        return !string.matches(regex);
    }

}
