package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import exceptions.ApplicationException;
import exceptions.InputException;
import exceptions.ResourceNotFoundException;
import models.Currency;
import models.ExchangeRate;
import models.ExchangeResult;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final CurrencyDao currencyDao;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(CurrencyDao currencyDao, ExchangeRateDao exchangeRateDao) {
        this.currencyDao = currencyDao;
        this.exchangeRateDao = exchangeRateDao;
    }

    private static ExchangeResult calculateExchange(Currency baseCurrency, Currency targetCurrency, double rate, double amount) {
        double convertedAmount = rate * amount;
        return new ExchangeResult(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private static ExchangeResult getIndirectExchange(ExchangeRate baseToUsd, ExchangeRate targetToUsd, double amount) {
        double basicCurrencyRate = baseToUsd.getRate();
        double targetCurrencyRate = targetToUsd.getRate();
        double rate = targetCurrencyRate / basicCurrencyRate;
        double convertedAmount = amount * rate;

        return new ExchangeResult(baseToUsd.getTargetCurrency(),
                targetToUsd.getTargetCurrency(), rate, amount, convertedAmount);
    }

    private static void validatePositive(double number) {
        if (number < 0.0) {
            throw new InputException("Rate must be more than 0.");
        }
    }

    private static void validateCurrencyCode(String currencyCode) {

        if (currencyCode.length() != 3) {
            throw new InputException("Currency code must have 3 symbols.");
        }

    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRateDao.getAll();
    }

    public ExchangeRate getExchangeRate(String basicCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.getByPair(basicCurrencyCode, targetCurrencyCode);
        return exchangeRate.orElseThrow(() -> new ResourceNotFoundException(
                String.format("Exchange rate with code %s%s not found", basicCurrencyCode, targetCurrencyCode)));
    }

    public ExchangeRate addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {

        validatePositive(rate);

        Currency basicCurrency = currencyDao.getByCode(baseCurrencyCode).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Currency with code %s not found.", baseCurrencyCode))
        );

        Currency targetCurrency = currencyDao.getByCode(targetCurrencyCode).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Currency with code %s not found.", targetCurrencyCode))
        );

        int exchangeRateId = exchangeRateDao.add(basicCurrency.getId(), targetCurrency.getId(), rate);

        ExchangeRate exchangeRate = new ExchangeRate(exchangeRateId, basicCurrency, targetCurrency, rate);
        return exchangeRate;
    }

    public ExchangeRate updateExchangeRates(String baseCurrencyCode, String targetCurrencyCode, double rate) {

        validatePositive(rate);

        Currency basicCurrency = currencyDao.getByCode(baseCurrencyCode).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Currency with code %s not found.", baseCurrencyCode))
        );

        Currency targetCurrency = currencyDao.getByCode(targetCurrencyCode).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Currency with code %s not found.", targetCurrencyCode))
        );

        exchangeRateDao.update(basicCurrency.getId(), targetCurrency.getId(), rate);

        Optional<ExchangeRate> exchangeRate = exchangeRateDao.getByPair(baseCurrencyCode, targetCurrencyCode);

        // HACK: это может сломать логику если в процессе выполнения апдейта кто-то удалил запись
        // TODO: Инкупсулировать эту штуку на уровне БД
        return exchangeRate.orElseThrow(
                () -> new ApplicationException("An error occurred while updating the exchange rate.")
        );

    }

    public ExchangeResult exchangeCurrency(String baseCurrencyCode, String targetCurrencyCode, double amount) {

        // validation
        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
        validatePositive(amount);

        // first method
        Optional<ExchangeRate> directRate = exchangeRateDao.getByPair(baseCurrencyCode, targetCurrencyCode);
        if (directRate.isPresent()) {
            ExchangeRate er = directRate.get();
            double rate = er.getRate();
            return calculateExchange(er.getBaseCurrency(), er.getTargetCurrency(), rate, amount);
        }

        // second method
        Optional<ExchangeRate> inverseRate = exchangeRateDao.getByPair(targetCurrencyCode, baseCurrencyCode);
        if (inverseRate.isPresent()) {
            ExchangeRate er = inverseRate.get();
            double rate = 1.0 / er.getRate();
            return calculateExchange(er.getBaseCurrency(), er.getTargetCurrency(), rate, amount);
        }

        // third method
        Optional<ExchangeRate> baseToUsd = exchangeRateDao.getByPair("USD", baseCurrencyCode);
        Optional<ExchangeRate> targetToUsd = exchangeRateDao.getByPair("USD", targetCurrencyCode);

        if (baseToUsd.isPresent() && targetToUsd.isPresent()) {
            return getIndirectExchange(baseToUsd.get(), targetToUsd.get(), amount);
        }

        throw new ResourceNotFoundException(String.format("Exchange rate %s%s not found.", baseCurrencyCode, targetCurrencyCode));

    }
}
