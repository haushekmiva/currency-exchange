package dao;

import models.ExchangeRate;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface ExchangeRateDao {
    List<ExchangeRate> getAll();

    Optional<ExchangeRate> getByPair(String baseCurrencyCode, String targetCurrencyCode);

    OptionalInt add(int baseCurrencyId, int targetCurrencyId, double rate);

    void update(int baseCurrencyId, int targetCurrencyId, double rate);
}
