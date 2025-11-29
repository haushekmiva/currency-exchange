import dao.CurrenciesDAO;
import dao.DataBaseManager;
import model.Currency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;


/**
 * TODO: добавить второй класс дао для обменных курсов (на понимание) и после подумать об абстракции.
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:data.db";
        try (Connection connection = DriverManager.getConnection(url)) { // подключение к бд

            // создание таблицы
            try (Statement stmt = connection.createStatement()) { // работа с бд через try/catch try закрывает соед.
                stmt.execute("PRAGMA foreign_keys = ON;");

                stmt.execute("""
                                CREATE TABLE IF NOT EXISTS currencies (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                code VARCHAR,
                                full_name VARCHAR,
                                sign VARCHAR
                            );
                        """);

                stmt.execute("""
                                CREATE TABLE IF NOT EXISTS exchange_rates (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                base_currency_id INTEGER,
                                target_currency_id INTEGER,
                                rate DECIMAL(6),
                                    FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
                                    FOREIGN KEY (target_currency_id) REFERENCES currencies(id)
                            );
                        """);

            }

            DataBaseManager manager = new DataBaseManager(url);
            CurrenciesDAO currencyDAO = new CurrenciesDAO(manager);
            OptionalInt id = currencyDAO.add("US", "Dollar US", "$");
            if (id.isPresent()) {
                System.out.println(id.getAsInt());
            } else System.out.println("Error");


            Optional<Currency> currency = currencyDAO.get(2);
            if (currency.isPresent()) {
                System.out.println(currency.get());
            } else System.out.println("Nothing");


            List<Currency> currencies = currencyDAO.getAll();
            for (Currency c : currencies) {
                System.out.println(c);
            }

        }


    }
}