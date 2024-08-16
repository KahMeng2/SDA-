package com.unimelb.swen90007.reactexampleapi.domain;

import java.time.OffsetDateTime;

public class UpdateVoteRequest {
    private String name;
    private String email;
    private Boolean supporting;
    private Vote.Status status;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSupporting() {
        return supporting;
    }

    public void setSupporting(Boolean supporting) {
        this.supporting = supporting;
    }

    public Vote.Status getStatus() {
        return status;
    }

    public void setStatus(Vote.Status status) {
        this.status = status;
    }
}
