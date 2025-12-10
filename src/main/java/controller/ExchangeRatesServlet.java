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
import utils.JsonMapper;
import static validation.FormatValidationUtils.checkNotEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

        String jsonResponse = JsonMapper.toJson(exchangeRates);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(jsonResponse);
        }

    }



}
