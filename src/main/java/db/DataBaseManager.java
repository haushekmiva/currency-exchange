package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {
    private final String URL;

    public DataBaseManager(String url) {
        this.URL = url;
    }


    public Connection connection() throws SQLException {
        return DriverManager.getConnection(URL);
    }


}
