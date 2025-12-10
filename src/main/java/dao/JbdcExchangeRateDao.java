package dao;

import db.DataBaseManager;
import exceptions.DataAccessException;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import models.ExchangeRate;
import utils.ExchangeRateCreator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JbdcExchangeRateDao implements ExchangeRateDao {
    private final DataBaseManager manager;
    private final String tableName = "exchange_rates"; // потом внести в конфиг


    public JbdcExchangeRateDao(DataBaseManager manager) {
        this.manager = manager;
    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String sql = """
                SELECT er.id AS exchange_rate_id,
                       bc.id AS base_currency_id,
                       bc.code AS base_currency_code,
                       bc.full_name AS base_currency_full_name,
                       bc.sign AS base_currency_sign,
                       tc.id AS target_currency_id,
                       tc.code AS target_currency_code,
                       tc.full_name AS target_currency_full_name,
                       tc.sign AS target_currency_sign,
                       er.rate AS rate
                FROM exchange_rates er
                JOIN currencies bc ON er.base_currency_id = bc.id
                JOIN currencies tc ON er.target_currency_id = tc.id
                """;

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                ExchangeRate exchangeRate = ExchangeRateCreator.create(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            String message = "Failed to fetch rates from exchange_rates.";
            throw new DataAccessException(message, e);
        }
    }


    @Override
    public Optional<ExchangeRate> getByPair(String baseCurrencyCode, String targetCurrencyCode) {
        String sql = """
                SELECT er.id AS exchange_rate_id,
                       bc.id AS base_currency_id,
                       bc.code AS base_currency_code,
                       bc.full_name AS base_currency_full_name,
                       bc.sign AS base_currency_sign,
                       tc.id AS target_currency_id,
                       tc.code AS target_currency_code,
                       tc.full_name AS target_currency_full_name,
                       tc.sign AS target_currency_sign,
                       er.rate AS rate
                FROM exchange_rates er
                JOIN currencies bc ON er.base_currency_id = bc.id
                JOIN currencies tc ON er.target_currency_id = tc.id
                WHERE bc.code = ? AND tc.code = ?
                """;
        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {

                    ExchangeRate exchangeRate = ExchangeRateCreator.create(resultSet);
                    return Optional.of(exchangeRate);
                }
                return Optional.empty();

            }

        } catch (SQLException e) {
            String message = String.format("Error fetching exchange rate for pair %s%s", baseCurrencyCode, targetCurrencyCode);
            throw new DataAccessException(message, e);
        }
    }

    @Override
    public int add(int baseCurrencyId, int targetCurrencyId, double rate) {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, baseCurrencyId);
            stmt.setInt(2, targetCurrencyId);
            stmt.setDouble(3, rate);

            stmt.executeUpdate();
            try (ResultSet resultSet = stmt.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else throw new DataAccessException("Failed to retrieve generated ID after inserting exchange rate.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new DuplicateResourceException(String.format(
                        "Exchange rate for currencies with id %d and %d already exists",
                        baseCurrencyId, targetCurrencyId), e);
            } else {
                System.out.println(e.getMessage());
                throw new DataAccessException(String.format(
                        "Failed to add exchange rate for currencies with ids %d and %d",
                        baseCurrencyId, targetCurrencyId), e);
            }
        }
    }

    @Override
    public void update(int baseCurrencyId, int targetCurrencyId, double rate) {
        String sql = "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, rate);
            stmt.setInt(2, baseCurrencyId);
            stmt.setInt(3, targetCurrencyId);
            int rowsUpdate = stmt.executeUpdate();
            if (rowsUpdate == 0) {
                throw new ResourceNotFoundException(String.format(
                        "Rate for currencies with ids %d and %d wasn't found in DB.",
                        baseCurrencyId, targetCurrencyId));
            }
        } catch (SQLException e) {
            String message = String.format("Exchange rate for pair %d/%d not found.", baseCurrencyId, targetCurrencyId);
            throw new DataAccessException(message, e);
        }
    }

}

