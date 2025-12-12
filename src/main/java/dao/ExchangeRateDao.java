package dao;

import models.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {
    List<ExchangeRate> getAll();

    Optional<ExchangeRate> getByPair(String baseCurrencyCode, String targetCurrencyCode);

    int add(int baseCurrencyId, int targetCurrencyId, double rate);

    void update(int baseCurrencyId, int targetCurrencyId, double rate);
}
