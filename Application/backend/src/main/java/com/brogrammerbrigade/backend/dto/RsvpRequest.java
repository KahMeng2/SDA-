package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class RsvpRequest {
    private BigInteger rsvpStudentId;
    private BigInteger eventId;
    private List<TicketRequest> ticketRequests;

    // Getters and setters for RsvpRequest fields
    public BigInteger getRsvpStudentId() {
        return rsvpStudentId;
    }

    public void setRsvpStudentId(BigInteger rsvpStudentId) {
        this.rsvpStudentId = rsvpStudentId;
    }

    public BigInteger getEventId() {
        return eventId;
    }

    public void setEventId(BigInteger eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("tickets")
    public List<TicketRequest> getTicketRequests() {
        return ticketRequests;
    }

    public void setTicketRequests(List<TicketRequest> ticketRequests) {
        this.ticketRequests = ticketRequests;
    }

    public void addTicketRequest(BigInteger ticketStudentId, BigInteger updatedTicketStudentId, String specialPreferences) {
        if (ticketRequests == null) {
            ticketRequests = new ArrayList<>();
        }
        ticketRequests.add(new TicketRequest(ticketStudentId, specialPreferences));
    }
}
