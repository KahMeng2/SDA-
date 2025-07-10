package com.brogrammerbrigade.backend.domain.FundingApplication;

public enum FundingApplicationStatus {
    IN_DRAFT("In Draft"),
    SUBMITTED("Submitted"),
    IN_REVIEW("In Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String value;

    FundingApplicationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}