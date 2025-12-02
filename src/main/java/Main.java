import dao.CurrenciesDAO;
import dao.DataBaseManager;
import dao.ExchangeRateDAO;
import exceptions.DataBaseException;
import model.Currency;
import model.ExchangeRate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;


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
                                sign VARCHAR,
                                UNIQUE(code)
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
                                    UNIQUE(base_currency_id, target_currency_id)
                            );
                        """);

            }

            DataBaseManager manager = new DataBaseManager(url);
            ExchangeRateDAO erDAO = new ExchangeRateDAO(manager);
            List<ExchangeRate> rates = erDAO.getAll();
            System.out.println(rates + "44");

           // try {
                erDAO.add(1, 2, 13241234);
            //} catch (DataBaseException e) {
                //System.out.println(e.getMessage());
            //}

            Optional<ExchangeRate> rate = erDAO.getByPair("RUB", "KZH");
            System.out.println(rate.get());
            }

        }


    }
