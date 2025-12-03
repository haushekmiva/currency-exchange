package utils;

import dao.DataBaseManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init(DataBaseManager manager) throws SQLException{

        try (Connection connection = manager.connection()) { // подключение к бд

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
                                    FOREIGN KEY (target_currency_id) REFERENCES currencies(id),
                                    UNIQUE(base_currency_id, target_currency_id)
                            );
                        """);

            }

        }
    }
}