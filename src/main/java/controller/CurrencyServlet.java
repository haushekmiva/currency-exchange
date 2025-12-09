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
import validation.PreconditionValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext ctx = getServletContext();
        this.currencyService = (CurrencyService) ctx.getAttribute("currencyService");
    }

    // TODO: сделать рефактор. вынести проверку пути в отдельную утилиту. разобраться с выбросом исключения при отправки хуйни.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.length() > 1) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String currencyCode = pathParts[1];
                PreconditionValidator.validateGetCurrencyArguments(currencyCode);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Currency currency = currencyService.getCurrencyByCode(currencyCode);
                String jsonResponse = JsonMapper.toJson(currency);

                PrintWriter printWriter = response.getWriter();
                printWriter.print(jsonResponse);
                printWriter.close();
            }
        } else {
            response.sendError(404, "Page not found.");
        }

    }

}
