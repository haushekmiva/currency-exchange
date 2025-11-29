package model;

public class ExchangeRate {
    private int id;
    private int baseCurrencyId;
    private int targetCurrencyId;
    private double rate;

    public ExchangeRate(int id, int baseCurrencyId, int targetCurrencyId, double rate) {
        this.rate = rate;
        this.targetCurrencyId = targetCurrencyId;
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
    }

    public double getRate() {
        return rate;
    }

    public int getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                '}';
    }
}
