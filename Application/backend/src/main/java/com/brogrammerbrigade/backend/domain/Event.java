package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.RsvpMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;

public class Event extends DomainObject {
    private BigInteger id;
    private Club club;
    private Venue venue;
    private List<Rsvp> rsvps;
    private Boolean isCancelled;
    private Boolean isOnline;
    private String name;
    private String description;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double cost;
    private Integer numTickets;
    private Integer capacity;

    public Event() {}

    public Event(BigInteger id) {
        this.id = id;
    }

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonIgnore
    public Club getClub() {
        if (this.club == null) {

            load();
        }
        return this.club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    @JsonIgnore
    public Venue getVenue() {
        if (this.venue == null) {
            load();
        }
        return this.venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    @JsonIgnore
    public List<Rsvp> getRsvps() {
        if (this.rsvps == null) {
            RsvpMapper rsvpMapper = RsvpMapper.getInstance();
            this.rsvps = rsvpMapper.getRsvpsForEvent(this);
        }
        return this.rsvps;
    }

    public void setRsvps(List<Rsvp> rsvps) {
        this.rsvps = rsvps;
    }

    @JsonProperty("isOnline")
    public Boolean isOnline() {
        if (this.isOnline == null) {
            load();
        }
        return isOnline;
    }

    public void setOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    @JsonProperty("isCancelled")
    public Boolean isCancelled() {
        if (this.isCancelled == null) {
            load();
        }
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    @JsonProperty("name")
    public String getName() {
        if (this.name == null) {
            load();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        if (this.description == null) {
            load();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("startTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public OffsetDateTime getStartTime() {
        if (this.startTime == null) {
            load();
        }
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("endTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public OffsetDateTime getEndTime() {
        if (this.endTime == null) {
            load();
        }
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("cost")
    public Double getCost() {
        if (this.cost == null) {
            load();
        }
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @JsonProperty("numTickets")
    public Integer getNumTickets() {
        if (this.numTickets == null) {
            load();
        }
        return numTickets;
    }

    public void setNumTickets(Integer numTickets) {
        this.numTickets = numTickets;
    }

    @JsonProperty("capacity")
    public Integer getCapacity() {
        if (this.capacity == null) {
            load();
        }
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @JsonProperty("clubId")
    public BigInteger getClubId() {
        return getClub().getId();
    }

    @JsonProperty("venueId")
    public BigInteger getVenueId() {
        return getVenue().getId();
    }

    public void setOnlineVenueId(BigInteger venueId) {
        if (this.venue == null) {
            this.venue = new OnlineVenue(venueId);
        } else {
            this.venue.setId(venueId);
        }
    }

    public void setPhysicalVenueId(BigInteger venueId) {
        if (this.venue == null) {
            this.venue = new PhysicalVenue(venueId);
        } else {
            this.venue.setId(venueId);
        }
    }

    @JsonProperty("clubName")
    public String getClubName() {
        return getClub().getName();
    }

    public void setClubName(String clubName) {
        getClub().setName(clubName);
    }

    public boolean exists(){
        EventMapper eventMapper = EventMapper.getInstance();
        Event loadedEvent = eventMapper.getEvent(this);
        return loadedEvent != null;
    }

    private void load() {
        if (this.id != null) {
            EventMapper eventMapper = EventMapper.getInstance();
            Event loadedEvent = eventMapper.getEvent(this);
            System.out.println("loading event");

            if (loadedEvent != null) {
                if (this.club == null) {
                    this.club = loadedEvent.getClub();
                }
                if (this.venue == null) {
                    this.venue = loadedEvent.getVenue();
                }
                if (this.isCancelled == null) {
                    this.isCancelled = loadedEvent.isCancelled();
                }
                if (this.isOnline == null) {
                    this.isOnline = loadedEvent.isOnline();
                }
                if (this.name == null) {
                    this.name = loadedEvent.getName();
                }
                if (this.description == null) {
                    this.description = loadedEvent.getDescription();
                }
                if (this.startTime == null) {
                    this.startTime = loadedEvent.getStartTime();
                }
                if (this.endTime == null) {
                    this.endTime = loadedEvent.getEndTime();
                }
                if (this.cost == null) {
                    this.cost = loadedEvent.getCost();
                }
                if (this.numTickets == null) {
                    this.numTickets = loadedEvent.getNumTickets();
                }
                if (this.capacity == null) {
                    this.capacity = loadedEvent.getCapacity();
                }
            }
        }
    }
}