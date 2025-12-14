package controller;

import exceptions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.ErrorMessage;
import utils.JsonMapper;

import java.io.IOException;
import jakarta.servlet.Filter;

import static utils.ResponseSender.sendResponse;

@WebFilter("/*")
public class ErrorHandlingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (InputException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DuplicateResourceException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (DataAccessException | ApplicationException e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Throwable e) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Occurred server error.");
        }


    }


    private void sendErrorResponse(HttpServletResponse response, int status, String message) {
        try {
            if (!response.isCommitted()) {
                // response.reset() мы опускаем специально, чтобы не стирались CORS заголовки
                ErrorMessage errorMessage = new ErrorMessage(message);
                sendResponse(response, errorMessage, status);
            }
        } catch (IOException e) {
            System.err.println("Failed to send error response: " + e.getMessage());
        }
    }

}
