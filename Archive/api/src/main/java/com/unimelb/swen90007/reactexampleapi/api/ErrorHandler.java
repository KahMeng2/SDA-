package com.unimelb.swen90007.reactexampleapi.api;


import com.unimelb.swen90007.reactexampleapi.domain.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler {
    private final HttpServletResponse response;
    private static ObjectMapper mapper = new ObjectMapper();

    private ErrorHandler(HttpServletResponse response, ObjectMapper mapper) {
        this.response = response;
        ErrorHandler.mapper = mapper;
    }

    public static ErrorHandler of(HttpServletResponse response) {
        return new ErrorHandler(response, mapper);
    }

    public void handle(Exception e) {
        try {
            response.setContentType("application/json");
            if (e instanceof ValidationException) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            mapper.writeValue(response.getOutputStream(), errorResponse);
        } catch (IOException ioException) {
            // Log the error
            ioException.printStackTrace();
        }
    }
}