package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import service.CurrencyService;
import utils.JsonMapper;
import static validation.FormatValidationUtils.checkNotEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;


    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        this.currencyService = (CurrencyService) ctx.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        List<Currency> currencies = currencyService.getAllCurrencies();

        String jsonResponse = JsonMapper.toJson(currencies);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonResponse);
        printWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String currencyName = request.getParameter("name");
        String currencyCode = request.getParameter("code");
        String currencySign = request.getParameter("sign");

        checkNotEmpty(currencyName, "name");
        checkNotEmpty(currencyCode, "code");
        checkNotEmpty(currencySign, "sign");

        Currency currency = currencyService.addCurrency(currencyCode, currencyName, currencySign);

        String jsonResponse = JsonMapper.toJson(currency);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.print(jsonResponse);
        printWriter.close();
    }
}


