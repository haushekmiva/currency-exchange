package controller;

import extractors.InputExtractor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeResult;
import service.ExchangeRateService;

import java.io.IOException;

import static utils.ResponseSender.sendResponse;
import static validation.FormatValidationUtils.checkNotEmpty;

@WebServlet("/exchange")
public class exchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;

    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        exchangeRateService = (ExchangeRateService) ctx.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        String amountRaw = request.getParameter("amount");

        checkNotEmpty(baseCurrencyCode, "from");
        checkNotEmpty(targetCurrencyCode, "for");
        checkNotEmpty(amountRaw, "amount");

        double amount = InputExtractor.extractDouble(amountRaw, "amount");

        ExchangeResult exchangeResult = exchangeRateService.exchangeCurrency(baseCurrencyCode, targetCurrencyCode, amount);

        sendResponse(response, exchangeResult);
    }

}
