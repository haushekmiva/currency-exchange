package dao;

import exceptions.DataBaseException;
import model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExchangeRateDAO {
    private DataBaseManager manager;

    public ExchangeRateDAO(DataBaseManager manager) {
        this.manager = manager;
    }

    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String sql = "SELECT * FROM exchange_rates";
        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int baseCurrencyId = resultSet.getInt("base_currency_id");
                int targetCurrencyId = resultSet.getInt("target_currency_id");
                double rate = resultSet.getDouble("rate");
                ExchangeRate exchangeRate = new ExchangeRate(id, baseCurrencyId, targetCurrencyId, rate);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DataBaseException("Failed to fetch data from database", e);
        }
    }


    public Optional<ExchangeRate> get(int id) {
        String sql = "SELECT * FROM exchange_rates WHERE id = ?";

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int baseCurrencyId = resultSet.getInt("base_currency_id");
                    int targetCurrencyId = resultSet.getInt("target_currency_id");
                    double rate = resultSet.getDouble("rate");
                    ExchangeRate exchangeRate = new ExchangeRate(id, baseCurrencyId, targetCurrencyId, rate);

                    // optional это такая оберточка которую юзаем что не использовать null
                    return Optional.of(exchangeRate);
                }
                return Optional.empty();

            }

        } catch (SQLException e) {
            throw new DataBaseException("Failed to fetch data from database", e);
        }
    }

    public OptionalInt add(int baseExchangeId, int targetExchangeId, double rate) {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, baseExchangeId);
            stmt.setInt(2, targetExchangeId);
            stmt.setDouble(3, rate);

            stmt.executeUpdate();

            try (ResultSet resultSet = stmt.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return OptionalInt.of(resultSet.getInt(1));
                } else return OptionalInt.empty();
            }

        } catch (SQLException e) {
            throw new DataBaseException("Failed to add data to database", e);
        }
    }


    public void update(int id, double rate) {
        String sql = "UPDATE exchange_rates SET rate = ? WHERE id = ?";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, rate);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException("Failed to update data in database", e);
        }

    }

}

