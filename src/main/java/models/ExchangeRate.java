package models;

// Предполагаем, что класс Currency также был преобразован в record
public record ExchangeRate(
        int id,
        Currency baseCurrency,
        Currency targetCurrency,
        double rate
) {}