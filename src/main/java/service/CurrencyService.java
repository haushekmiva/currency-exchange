package service;

import dao.CurrencyDao;
import exceptions.InputException;
import models.Currency;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.RejectedExecutionException;


public class CurrencyService {
    CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.getAll();
    }

    public Currency getCurrencyByCode(String code) {
        Optional<Currency> currency = currencyDao.getByCode(code);
        if (currency.isPresent()) {
            return currency.get();
        } else {
            throw new RejectedExecutionException(String.format("Currency with code %s wasn't found.", code));
        }
    }

    public OptionalInt addCurrency(String code, String fullName, String sign) {

        //------временная заглушка, неоптимизированная валидация------
        if (isNotValid(code, "[A-Z]{3}$")) {
            throw new InputException("Currency code is incorrect.");
        }

        if (isNotValid(fullName, "^[a-zA-Z0-9\s]{3,10}")) {
            throw new InputException("Currency full name is incorrect");
        }

        if (isNotValid(sign, ".{1,5}$")) {
            throw new InputException("Currency sign is incorrect");
        }
        //-----------------------------------------------------------

        OptionalInt generated_keys = currencyDao.add(code, fullName, sign);
        if (generated_keys.isPresent()) {
            return generated_keys;
        } else {
            return OptionalInt.empty();
        }
    }

    private boolean isNotValid(String string, String regex) {
        return !string.matches(regex);
    }

}
