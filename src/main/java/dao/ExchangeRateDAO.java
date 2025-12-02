package dao;

import exceptions.DataBaseException;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import model.ExchangeRate;
import utils.ExchangeRateCreator;
import utils.LogMessageCreator;
import utils.MessageType;
import utils.OperationType;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExchangeRateDAO {
    private DataBaseManager manager;
    private String tableName = "exchange_rates"; // потом внести в конфиг


    public ExchangeRateDAO(DataBaseManager manager) {
        this.manager = manager;
    }

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
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.GET, tableName,
                    LogMessageCreator.NO_SPECIFIC_CODE);
            throw new DataBaseException(message, e);
        }
    }


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

                    // optional это такая оберточка которую юзаем что не использовать null
                    return Optional.of(exchangeRate);
                }
                return Optional.empty();

            }

        } catch (SQLException e) {
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.GET, tableName,
                    baseCurrencyCode + targetCurrencyCode);
            throw new DataBaseException(message, e);
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
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.ADD, tableName,
                LogMessageCreator.NO_SPECIFIC_CODE);
            if ("23505".equals(e.getSQLState())) {
                throw new DuplicateResourceException(message, e);
            } else {
                throw new DataBaseException(message, e);
            }
        }
    }

    public void update(String basicCurrencyId, String targetCurrencyId, double rate) {
        String sql = "UPDATE exchange_rates SET rate = ? WHERE basic_currency_id = ? AND target_currency_id = ?";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, rate);
            stmt.setString(2, basicCurrencyId);
            stmt.setString(3, targetCurrencyId);
            int rowsUpdate = stmt.executeUpdate();
            if (rowsUpdate == 0) {
                throw new ResourceNotFoundException("This rate wasn't found in db");
            }
        } catch (SQLException e) {
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.UPDATE, tableName, LogMessageCreator.NO_SPECIFIC_CODE);
            throw new DataBaseException(message, e);
        }
    }

}

