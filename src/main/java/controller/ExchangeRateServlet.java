package controller;

import exceptions.InputException;
import exceptions.ResourceNotFoundException;
import extractors.InputExtractor;
import extractors.PathExtractor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.CurrencyPair;
import models.ExchangeRate;
import service.ExchangeRateService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static utils.ResponseSender.sendResponse;
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
        CurrencyPair currencyPair = extractCurrencyPair(request);
        String baseCurrencyCode = currencyPair.baseCurrencyCode();
        String targetCurrencyCode = currencyPair.targetCurrencyCode();

        ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);

        sendResponse(response, exchangeRate);

    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {


        InputStream inputStream = request.getInputStream();
        CurrencyPair currencyCodesRaw = extractCurrencyPair(request);

        String rateRaw = InputExtractor.extractArgumentFromInputStream(inputStream, "rate");
        String baseCurrencyCode = currencyCodesRaw.baseCurrencyCode();
        String targetCurrencyCode = currencyCodesRaw.targetCurrencyCode();

        checkNotEmpty(baseCurrencyCode, "baseCurrencyCode");
        checkNotEmpty(targetCurrencyCode, "targetCurrencyCode");
        checkNotEmpty(rateRaw, "rate");

        double rate = InputExtractor.extractDouble(rateRaw, "rate");
        ExchangeRate exchangeRate = exchangeRateService.updateExchangeRates(baseCurrencyCode, targetCurrencyCode, rate);

        sendResponse(response, exchangeRate);

    }

    private CurrencyPair extractCurrencyPair(HttpServletRequest request) {
        String path = request.getPathInfo();
        Optional<String> pathSegment = PathExtractor.extractFirstPathSegment(path);

        if (pathSegment.isEmpty()) {
            throw new InputException("Currencies codes required.");
        }

        String currenciesCodes = pathSegment.get();
        checkNotEmpty(currenciesCodes, "currency codes");

        if (currenciesCodes.length() != 6) {
            throw new InputException("Invalid currency pair format.");
        }

        String baseCurrencyCode = currenciesCodes.substring(0, 3);
        String targetCurrencyCode = currenciesCodes.substring(3, 6);
        return new CurrencyPair(baseCurrencyCode, targetCurrencyCode);
    }


}
