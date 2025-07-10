package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventFilterRequest {
    private String nameQuery;
    private Boolean isUpcoming;
    private Boolean isOnline;
    private Boolean isCancelled;

    @JsonProperty("nameQuery")
    public String getNameQuery() {
        return nameQuery;
    }

    public void setNameQuery(String nameQuery) {
        this.nameQuery = nameQuery;
    }

    @JsonProperty("isUpcoming")
    public Boolean getUpcoming() {
        return isUpcoming;
    }

    public void setUpcoming(Boolean upcoming) {
        isUpcoming = upcoming;
    }

    @JsonProperty("isOnline")
    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    @JsonProperty("isCancelled")
    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }
}
