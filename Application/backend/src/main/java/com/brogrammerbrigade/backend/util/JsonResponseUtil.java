package com.brogrammerbrigade.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Register the JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static void sendJsonResponse(HttpServletResponse resp, Object object, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        if (object != null) {
            objectMapper.writeValue(resp.getWriter(), object);
        } else {
            resp.getWriter().write("");  // Explicitly write an empty body
        }
    }

    public static void sendErrorResponse(HttpServletResponse resp, String message, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        objectMapper.writeValue(resp.getWriter(), new ErrorResponse(message));
    }

    public static void sendErrorResponse(HttpServletResponse resp, int status) throws IOException {
        resp.sendError(status);
    }

    public record ErrorResponse(String error) {
    }
}