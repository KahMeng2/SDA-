package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.OnlineVenueMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

public class OnlineVenue extends Venue {
    private BigInteger id;
    private String link;

    // No id constructor for insertions
    public OnlineVenue() {
    }

    @Override
    public void load() {
        OnlineVenue data = OnlineVenueMapper.getInstance().getOnlineVenueById(this.id);
        super.loadParent(data);
        if (link == null){
            link = data.getLink();
        }
    }

    // Constructor with id
    public OnlineVenue(BigInteger id) {
        this.id = id;
    }

    // Getters and Setters
    @Override
    @JsonProperty("id")
    public BigInteger getId() { return id; }

    @Override
    public void setId(BigInteger id) { this.id = id; }

    @JsonIgnore
    @Override
    public List<Event> getEvents() {
        if (this.events == null){
            this.setEvents(EventMapper.getInstance().getEventsForVenue(this.id,true));
        }
        return this.events;
    }

    @JsonProperty("link")
    public String getLink() {
        if (this.link == null){
            load();
        }
        return link;
    }
    public void setLink(String link) { this.link = link; }
}