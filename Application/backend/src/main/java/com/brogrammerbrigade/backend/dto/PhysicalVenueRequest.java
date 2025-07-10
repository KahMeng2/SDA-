package com.brogrammerbrigade.backend.dto;

import com.brogrammerbrigade.backend.domain.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class PhysicalVenueRequest {
    private BigInteger id;
    private String description;
    private Integer venueCapacity;
    private Double cost;
    private AddressRequest address;
    private String floor;
    private String room;

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    @JsonProperty("address")
    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }

    @JsonProperty("cost")
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @JsonProperty("floor")
    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @JsonProperty("room")
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

