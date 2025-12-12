package controller;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JbdcCurrencyDao;
import dao.JbdcExchangeRateDao;
import db.DataBaseManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.CurrencyService;
import service.ExchangeRateService;
import utils.DatabaseInitializer;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // подгружаем драйвер бд вручную тк томкат это не делает
            Class.forName("org.sqlite.JDBC");

            // получаем правильный путь к БД
            ServletContext ctx = sce.getServletContext();
            String relativePath = "/WEB-INF/data.db";
            String absolutePath = ctx.getRealPath(relativePath);

            // если absolutePath null, используем временный файл или жесткий путь для теста, но пока пробуем так:
            String dbUrl;
            if (absolutePath != null) {
                dbUrl = "jdbc:sqlite:" + absolutePath;
            } else dbUrl = "jdbc:sqlite:C:/Temp/data.db"; // На крайний случай, если путь не найдется


            DataBaseManager manager = new DataBaseManager(dbUrl);
            DatabaseInitializer.init(manager);

            CurrencyDao currencyDao = new JbdcCurrencyDao(manager);
            ExchangeRateDao exchangeRateDao = new JbdcExchangeRateDao(manager);

            CurrencyService currencyService = new CurrencyService(currencyDao);
            ExchangeRateService exchangeRateService = new ExchangeRateService(currencyDao, exchangeRateDao);

            ctx.setAttribute("currencyService", currencyService);
            ctx.setAttribute("exchangeRateService", exchangeRateService);

        } catch (ClassNotFoundException e) {
            // Если упало здесь - значит драйвера нет в WEB-INF/lib
            throw new RuntimeException("CRITICAL: SQLite Driver not found! Check dependencies.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing application: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}