package utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseSender {

    public static <T> void sendResponse(HttpServletResponse response, T jsonContent, int status) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = JsonMapper.toJson(jsonContent);

        PrintWriter printWriter = response.getWriter();
        response.setStatus(status);
        printWriter.print(jsonResponse);
    }

}
