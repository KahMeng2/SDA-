package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class TicketRequest {
    private BigInteger ticketStudentId;
    private String specialPreferences;

    @JsonCreator
    public TicketRequest(@JsonProperty("ticketStudentId") BigInteger ticketStudentId,
                         @JsonProperty("specialPreferences") String specialPreferences) {
        this.ticketStudentId = ticketStudentId;
        this.specialPreferences = specialPreferences;
    }
    // Getters and setters
    @JsonProperty("ticketStudentId")
    public BigInteger getTicketStudentId() {
        return ticketStudentId;
    }

    public void setTicketStudentId(BigInteger ticketStudentId) {
        this.ticketStudentId = ticketStudentId;
    }

    @JsonProperty("specialPreferences")
    public String getSpecialPreferences() {
        return specialPreferences;
    }

    public void setSpecialPreferences(String specialPreferences) {
        this.specialPreferences = specialPreferences;
    }
}
