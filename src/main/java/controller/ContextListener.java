package controller;

import dao.*;
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
            // 1. !!! САМОЕ ГЛАВНОЕ: ПРИНУДИТЕЛЬНО ЗАГРУЖАЕМ ДРАЙВЕР !!!
            // Без этой строки Tomcat часто "не видит" драйвер в WEB-INF/lib
            Class.forName("org.sqlite.JDBC");

            // 2. Получаем правильный путь к БД (как обсуждали раньше)
            // Убедитесь, что файл data.db лежит в src/main/webapp/WEB-INF/data.db
            ServletContext ctx = sce.getServletContext();
            String relativePath = "/WEB-INF/data.db";
            String absolutePath = ctx.getRealPath(relativePath);

            // Фолбэк: если absolutePath null (бывает в некоторых конфигах),
            // используем временный файл или жесткий путь для теста, но пока пробуем так:
            String dbUrl = (absolutePath != null)
                    ? "jdbc:sqlite:" + absolutePath
                    : "jdbc:sqlite:C:/Temp/data.db"; // На крайний случай, если путь не найдется

            System.out.println("=== DB URL: " + dbUrl + " ==="); // Смотрим в логи, какой путь получился

            DataBaseManager manager = new DataBaseManager(dbUrl);
            DatabaseInitializer.init(manager);

            CurrencyDao currencyDao = new JbdcCurrencyDao(manager);
            ExchangeRateDao exchangeRateDao = new JbdcExchangeRateDao(manager);

            CurrencyService currencyService = new CurrencyService(currencyDao);
            ExchangeRateService exchangeRateService = new ExchangeRateService(currencyDao, exchangeRateDao);

            ctx.setAttribute("currencyService", currencyService);
            ctx.setAttribute("exchangeRateService", exchangeRateService);

        } catch (ClassNotFoundException e) {
            // Если упало здесь - значит драйвера ТОЧНО нет в WEB-INF/lib
            throw new RuntimeException("CRITICAL: SQLite Driver not found! Check dependencies.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing application: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}