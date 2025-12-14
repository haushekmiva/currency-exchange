package models;

public record ExchangeRate(
        int id,
        Currency baseCurrency,
        Currency targetCurrency,
        double rate
) {
}