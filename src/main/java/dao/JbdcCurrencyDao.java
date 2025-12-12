package dao;

import db.DataBaseManager;
import exceptions.DataAccessException;
import exceptions.DuplicateResourceException;
import models.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JbdcCurrencyDao implements CurrencyDao {
    private final DataBaseManager manager;

    public JbdcCurrencyDao(DataBaseManager manager) {
        this.manager = manager;
    }


    /**
     * Метод для получения всех записей из таблицы currencies.
     *
     * @return список объектов Currency
     */
    @Override
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();

        String sql = "SELECT * FROM currencies";

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");
                Currency currency = new Currency(id, code, fullName, sign);
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            String message = "Failed to fetch currencies from currencies.";
            throw new DataAccessException(message, e);
        }
    }

    @Override
    public Optional<Currency> getByCode(String code) {

        String sql = "SELECT * FROM currencies WHERE code = ?"; // запросик для получения записи из бд где id = чему-то

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String fullName = resultSet.getString("full_name");
                    String sign = resultSet.getString("sign");
                    Currency currency = new Currency(id, code, fullName, sign);
                    return Optional.of(currency);
                } else return Optional.empty();
            }
        } catch (SQLException e) {
            String message = String.format("Currency %s not found.", code);
            throw new DataAccessException(message, e);
        }

    }

    @Override
    public Currency add(String code, String fullName, String sign) {
        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            try (ResultSet resultSet = stmt.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    Currency currency = new Currency(id, code, fullName, sign);
                    return currency;
                } else throw new DataAccessException("Failed to retrieve generated ID after inserting currency.");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new DuplicateResourceException(String.format("Currency %s already exists.", code), e);
            } else {
                throw new DataAccessException(String.format("Failed to add currency %s", code), e);
            }
        }
    }
}
