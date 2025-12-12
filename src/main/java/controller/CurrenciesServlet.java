package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;

import static utils.ResponseSender.sendResponse;
import static validation.FormatValidationUtils.checkNotEmpty;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        List<Currency> currencies = currencyService.getAllCurrencies();

        sendResponse(response, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currencyName = request.getParameter("name");
        String currencyCode = request.getParameter("code");
        String currencySign = request.getParameter("sign");

        checkNotEmpty(currencyName, "name");
        checkNotEmpty(currencyCode, "code");
        checkNotEmpty(currencySign, "sign");

        Currency currency = currencyService.addCurrency(currencyCode, currencyName, currencySign);

        response.setStatus(HttpServletResponse.SC_CREATED);
        sendResponse(response, currency);
    }
}


