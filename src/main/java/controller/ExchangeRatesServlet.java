package controller;

import extractors.InputExtractor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

import static utils.ResponseSender.sendResponse;
import static validation.FormatValidationUtils.checkNotEmpty;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        exchangeRateService = (ExchangeRateService) ctx.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {

        List<ExchangeRate> exchangeRates = exchangeRateService.getExchangeRates();
        sendResponse(response, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rateRaw = request.getParameter("rate");

        checkNotEmpty(baseCurrencyCode, "baseCurrencyCode");
        checkNotEmpty(targetCurrencyCode, "targetCurrencyCode");
        checkNotEmpty(rateRaw, "rate");
        double rate = InputExtractor.extractDouble(rateRaw, "rate");

        ExchangeRate exchangeRate = exchangeRateService.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

        response.setStatus(HttpServletResponse.SC_CREATED);
        sendResponse(response, exchangeRate);
    }

}
