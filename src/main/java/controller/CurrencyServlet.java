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
import validation.GetCurrencyPathValidator;
import validation.PreconditionValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        this.currencyService = (CurrencyService) ctx.getAttribute("currencyService");
    }

    // TODO: разобраться с выбросом исключения при отправки хуйни.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        Optional<String> currencyCodeOptional = GetCurrencyPathValidator.extractCurrencyCode(path);

        if (currencyCodeOptional.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found at this path."); // 404
            return;
        }
                String currencyCode = currencyCodeOptional.get();
                PreconditionValidator.validateGetCurrencyArguments(currencyCode);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Currency currency = currencyService.getCurrencyByCode(currencyCode);
                String jsonResponse = JsonMapper.toJson(currency);

                PrintWriter printWriter = response.getWriter();
                printWriter.print(jsonResponse);
                printWriter.close();

        }
    }
