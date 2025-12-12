package controller;

import extractors.PathExtractor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;

import static utils.ResponseSender.sendResponse;
import static validation.FormatValidationUtils.checkNotEmpty;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        this.currencyService = (CurrencyService) ctx.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        Optional<String> userInput = PathExtractor.extractFirstPathSegment(path);

        if (userInput.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found at this path."); // 404
            return;
        }
        String currencyCode = userInput.get();
        checkNotEmpty(currencyCode, "code");
        Currency currency = currencyService.getCurrencyByCode(currencyCode);

        sendResponse(response, currency);
    }
}
