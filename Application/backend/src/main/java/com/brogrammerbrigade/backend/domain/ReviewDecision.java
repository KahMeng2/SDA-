package com.brogrammerbrigade.backend.domain;

public enum ReviewDecision {
    Approved("Approved"),
    Rejected("Rejected");

    private final String value;

    ReviewDecision(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
