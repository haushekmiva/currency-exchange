package utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseSender {

    public static <T> void sendResponse(HttpServletResponse response, T jsonContent) throws IOException {
        String jsonResponse = JsonMapper.toJson(jsonContent);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(jsonResponse);
        }
    }

}
