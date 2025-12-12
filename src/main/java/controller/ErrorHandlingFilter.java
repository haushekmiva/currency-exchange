package controller;

import exceptions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ErrorMessage;
import utils.JsonMapper;

import java.io.IOException;
import jakarta.servlet.Filter;

@WebFilter("/*")
public class ErrorHandlingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (InputException | ReadFromJsonException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DuplicateResourceException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (WriteToJsonException | ApplicationException | DataAccessException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Throwable e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Occurred server error.");
        }


    }



    private void sendErrorResponse(HttpServletResponse response, int status, String message) {
        try {
            // Проверяем что ответ ещё не был отправлен
            if (!response.isCommitted()) {
                response.reset(); // Очищаем любые данные которые могли быть записаны
                response.setStatus(status);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // Более детальный формат ошибки
                ErrorMessage errorMessage = new ErrorMessage(message);
                String jsonResponse = JsonMapper.toJson(errorMessage);

                response.getWriter().write(jsonResponse);
            }
        } catch (IOException e) {
            System.err.println("Failed to send error response: " + e.getMessage());
        }
    }

}
