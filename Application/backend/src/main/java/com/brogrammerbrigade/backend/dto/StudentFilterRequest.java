package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentFilterRequest {
    private String nameQuery;

    @JsonProperty("nameQuery")
    public String getNameQuery() {
        return nameQuery;
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }
}
