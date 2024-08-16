package com.unimelb.swen90007.reactexampleapi.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.Supplier;

public class MarshallingRequestHandler {
    private final ObjectMapper mapper;
    private final HttpServletResponse response;
    private final ErrorHandler errorHandler;

    private MarshallingRequestHandler(ObjectMapper mapper, HttpServletResponse response, ErrorHandler errorHandler) {
        this.mapper = mapper;
        this.response = response;
        this.errorHandler = errorHandler;
    }

    public static MarshallingRequestHandler of(ObjectMapper mapper, HttpServletResponse response, ErrorHandler errorHandler) {
        return new MarshallingRequestHandler(mapper, response, errorHandler);
    }

    public <T> void handle(Supplier<ResponseEntity<T>> handler) {
        try {
            ResponseEntity<T> responseEntity = handler.get();
            response.setStatus(responseEntity.getStatus());
            response.setContentType("application/json");
            mapper.writeValue(response.getOutputStream(), responseEntity.getBody());
        } catch (Exception e) {
            errorHandler.handle(e);
        }
    }
}