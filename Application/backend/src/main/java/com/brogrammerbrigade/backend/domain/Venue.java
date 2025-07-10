package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

public abstract class Venue extends DomainObject {
    private String description;
    private Double cost;
    private Integer venueCapacity;
    protected List<Event> events;

    public Venue(){}

    public abstract void load();

    protected void loadParent(Venue loadedVenue){
        if (description == null){
            description = loadedVenue.getDescription();
        }
        if (cost == null){
            cost = loadedVenue.getCost();
        }
        if (venueCapacity == null){
            venueCapacity = loadedVenue.getVenueCapacity();
        }
    }

    public abstract BigInteger getId();
    public abstract void setId(BigInteger id);

    @JsonProperty("cost")
    public Double getCost() {
        if (cost == null){
            load();
        }
        return cost;
    }

    @JsonProperty("venueCapacity")
    public Integer getVenueCapacity() {
        if (venueCapacity == null){
            load();
        }
        return venueCapacity;
    }

    @JsonProperty("description")
    public String getDescription() {
        if (description == null){
            load();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setVenueCapacity(Integer venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    @JsonIgnore
    public abstract List<Event> getEvents();

    public void setEvents(List<Event> events) {
        this.events = events;
    }

//    public BigInteger getEventId() {
//        return getEvent().getId();
//    }
//
//    public void setEventId(BigInteger eventId) {
//        getEvent().setId(eventId);
//    }


}
