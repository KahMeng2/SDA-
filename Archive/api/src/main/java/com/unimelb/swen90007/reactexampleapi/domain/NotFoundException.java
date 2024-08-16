package com.unimelb.swen90007.reactexampleapi.domain;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}