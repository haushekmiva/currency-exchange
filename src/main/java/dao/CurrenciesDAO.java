package dao;

import exceptions.DataBaseException;
import exceptions.DuplicateResourceException;
import model.Currency;
import utils.LogMessageCreator;
import utils.MessageType;
import utils.OperationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class CurrenciesDAO  {
    private final DataBaseManager manager;
    private final String tableName = "currencies";

    public CurrenciesDAO(DataBaseManager manager) {
        this.manager = manager;
    }


    /**
     * Метод для получения всех записей из таблицы currencies.
     *
     * @return список объектов Currency
     */
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();

        String sql = "SELECT * FROM currencies"; // sql запрос для выбора ВСЕГО из таблицы

        // устанавливаем соединение с базой, создаем объект для выполнения запросов и таблица-ответ с курсором
        // try-catch-resource автоматически закрывает их чтобы не было утечек данных и превышения лимитов бд
        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {
            // тк курсор появляется за таблицей мы его сдвигаем
            while (resultSet.next()) {

                // и вытаскиваем каждое поле из записи
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");
                Currency currency = new Currency(id, code, fullName, sign);
                currencies.add(currency);
            }
            return currencies;
        } catch (SQLException e) {
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.GET, tableName,
                    LogMessageCreator.NO_SPECIFIC_CODE);
            throw new DataBaseException(message, e);
        }
    }

    public Optional<Currency> getByCode(String code) {

        String sql = "SELECT * FROM currencies WHERE code = ?"; // запросик для получения записи из бд где id = чему-то

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql)) {

            // заполняем вопросики и отправляем запрос
            stmt.setString(1, code);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String fullName = resultSet.getString("full_name");
                    String sign = resultSet.getString("sign");
                    Currency currency = new Currency(id, code, fullName, sign);

                    // optional это такая оберточка которую юзаем что не использовать null
                    return Optional.of(currency);
                } else return Optional.empty();
            }
        } catch (SQLException e) {
            String message = LogMessageCreator.createMessage(MessageType.FAILED, OperationType.GET, tableName, code);
            throw new DataBaseException(message, e);
        }

    }

    public OptionalInt add(String code, String fullName, String sign) {
        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        try (Connection connection = manager.connection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);
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
}
