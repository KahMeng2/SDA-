package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class OnlineVenueRequest {
    private BigInteger id;
    private String link;
    private String description;
    private Integer venueCapacity;
    private Double cost;

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("venueCapacity")
    public Integer getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(Integer venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    @JsonProperty("cost")
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
