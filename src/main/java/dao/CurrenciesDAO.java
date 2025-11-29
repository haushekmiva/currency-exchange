package dao;

import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class CurrenciesDAO  {
    private DataBaseManager manager;

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
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> get(int id) {

        String sql = "SELECT * FROM currencies WHERE id = ?"; // запросик для получения записи из бд где id = чему-то

        try (Connection connection = manager.connection(); PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery();) {

            // заполняем вопросики и отправляем запрос
            stmt.setInt(1, id);

            if (resultSet.next()) {
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");
                Currency currency = new Currency(id, code, fullName, sign);

                // optional это такая оберточка которую юзаем что не использовать null
                return Optional.of(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
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
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } return OptionalInt.empty();

    }
}
