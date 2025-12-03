import dao.*;
import utils.DatabaseInitializer;

import java.sql.SQLException;


public class Main {
    static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:data.db";
        DataBaseManager manager = new DataBaseManager(url);
        DatabaseInitializer.init(manager);

        ExchangeRateDao exchangeRateDao = new JbdcExchangeRateDao(manager);
        CurrencyDao currencyDao = new JbdcCurrencyDao(manager);

        currencyDao.add("RUB", "ruurru", "sdf");

    }

}



