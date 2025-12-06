package dao;

import models.Currency;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface CurrencyDao {
    List<Currency> getAll();

    Optional<Currency> getByCode(String code);

    Currency add(String code, String fullName, String sign);
}
