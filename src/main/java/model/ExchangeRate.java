package model;

public class ExchangeRate {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, double rate) {
        this.rate = rate;
        this.targetCurrency = targetCurrency;
        this.id = id;
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public double getRate() {
        return rate;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", rate=" + rate +
                '}';
    }
}
