package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;

public class EventRequest {
    private BigInteger id;
    private BigInteger clubId;
    private Boolean isCancelled;
    private Boolean isOnline;
    private BigInteger venueId;
    private String name;
    private String description;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double cost;
    private Integer numTickets;
    private Integer capacity;
    private List<String> attendees; // Assuming you have a list of attendees
    private String username;

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonProperty("clubId")
    public BigInteger getClubId() {
        return clubId;
    }

    public void setClubId(BigInteger clubId) {
        this.clubId = clubId;
    }

    @JsonProperty("isCancelled")
    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @JsonProperty("isOnline")
    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    @JsonProperty("venueId")
    public BigInteger getVenueId() {
        return venueId;
    }

    public void setVenueId(BigInteger venueId) {
        this.venueId = venueId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("startTime")
    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("endTime")
    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("cost")
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @JsonProperty("numTickets")
    public Integer getNumTickets() {
        return numTickets;
    }

    public void setNumTickets(Integer numTickets) {
        this.numTickets = numTickets;
    }

    @JsonProperty("capacity")
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @JsonProperty("attendees")
    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
