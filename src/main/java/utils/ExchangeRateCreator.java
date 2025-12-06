package utils;

import models.Currency;
import models.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateCreator {

    private ExchangeRateCreator() {
    }

    public static ExchangeRate create(ResultSet resultSet) throws SQLException {
        int exchangeRateId = resultSet.getInt("exchange_rate_id");

        int baseCurrencyId = resultSet.getInt("base_currency_id");
        String baseCurrencyFullName = resultSet.getString("base_currency_full_name");
        String baseCurrencyCode = resultSet.getString("base_currency_code");
        String baseCurrencySign = resultSet.getString("base_currency_sign");

        int targetCurrencyId = resultSet.getInt("target_currency_id");
        String targetCurrencyFullName = resultSet.getString("target_currency_full_name");
        String targetCurrencyCode = resultSet.getString("target_currency_code");
        String targetCurrencySign = resultSet.getString("target_currency_sign");

        double rate = resultSet.getDouble("rate");

        Currency baseCurrency = new Currency(baseCurrencyId, baseCurrencyCode, baseCurrencyFullName, baseCurrencySign);
        Currency targetCurrency = new Currency(targetCurrencyId, targetCurrencyCode, targetCurrencyFullName, targetCurrencySign);
        ExchangeRate exchangeRate = new ExchangeRate(exchangeRateId, baseCurrency, targetCurrency, rate);

        return exchangeRate;

    }

}
