package controller;

import exceptions.InputException;
import extractors.InputExtractor;
import extractors.PathExtractor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import service.ExchangeRateService;
import utils.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static validation.FormatValidationUtils.checkNotEmpty;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        exchangeRateService = (ExchangeRateService) ctx.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        Optional<String> pathSegment = PathExtractor.extractFirstPathSegment(path);

        if (pathSegment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found at this path.");
            return;
        }

        String currenciesCodes = pathSegment.get();
        checkNotEmpty(currenciesCodes, "code");

        if (currenciesCodes.length() != 6) {
            throw new InputException("Invalid currency pair format.");
        }

        String basicCurrencyCode = currenciesCodes.substring(0, 3);
        String targetCurrencyCode = currenciesCodes.substring(3, 6);

        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(basicCurrencyCode, targetCurrencyCode);

        String jsonResponse = JsonMapper.toJson(exchangeRate);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(jsonResponse);
        }

    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        Optional<String> pathSegment = PathExtractor.extractFirstPathSegment(path);

        if (pathSegment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found at this path.");
            return;
        }

        String currenciesCodes = pathSegment.get();
        checkNotEmpty(currenciesCodes, "currency codes");

        if (currenciesCodes.length() != 6) {
            throw new InputException("Invalid currency pair format.");
        }

        String baseCurrencyCode = currenciesCodes.substring(0, 3);
        String targetCurrencyCode = currenciesCodes.substring(3, 6);

        InputStream inputStream = request.getInputStream();
        String rateRaw = InputExtractor.extractArgumentFromInputStream(inputStream, "rate");


        checkNotEmpty(baseCurrencyCode, "baseCurrencyCode");
        checkNotEmpty(targetCurrencyCode, "targetCurrencyCode");

        // НЕ ПАРСИТСЯ САМО НИХУЯ, Я САМ ДОЛЖЕН ПАРСИТЬ ЭТО
        checkNotEmpty(rateRaw, "rate");

        double rate = InputExtractor.extractDouble(rateRaw, "rate");
        ExchangeRate exchangeRate = exchangeRateService.updateExchangeRates(baseCurrencyCode, targetCurrencyCode, rate);

        String jsonResponse = JsonMapper.toJson(exchangeRate);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(jsonResponse);
        }

    }



}
