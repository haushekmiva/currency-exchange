package utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseSender {

    public static <T> void sendResponse(HttpServletResponse response, T jsonContent, int status) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = JsonMapper.toJson(jsonContent);

        try (PrintWriter printWriter = response.getWriter()) {
            response.setStatus(status);
            printWriter.print(jsonResponse);
        }
    }

}
