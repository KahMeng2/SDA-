package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.RsvpMapper;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.datasource.TicketMapper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Optional;

public class Ticket extends DomainObject {
    private Event event;
    private Student rsvpStudent;
    private Student ticketStudent;
    private String specialPreferences;

    public Ticket() {
    }

    @JsonCreator
    public Ticket(BigInteger eventId, BigInteger rsvpStudentId, BigInteger ticketStudentId) {
        this.event = new Event(eventId);
        this.rsvpStudent = new Student(rsvpStudentId);
        this.ticketStudent = new Student(ticketStudentId);
    }

    private void load() {
        System.out.println("Loading ticket");
        TicketMapper ticketMapper = TicketMapper.getInstance();
        Ticket ticketData = ticketMapper.getTicket(this);

        if (rsvpStudent == null) {
            setRsvpStudent(ticketData.getRsvpStudent());
        }

        if (ticketStudent == null) {
            setTicketStudent(ticketData.getTicketStudent());
        }

        if (event == null) {
            setEvent(ticketData.getEvent());
        }

        if (specialPreferences == null) {
            setSpecialPreferences(ticketData.getSpecialPreferences());
        }
    }

    @JsonProperty("eventId")
    public BigInteger getEventId() {
        return getEvent().getId();
    }

    public void setEventId(BigInteger eventId) {
        getEvent().setId(eventId);
    }

    @JsonProperty("rsvpStudentId")
    public BigInteger getRsvpStudentId() {
        return getRsvpStudent().getId();
    }

    public void setRsvpStudentId(BigInteger rsvpStudentId) {
        getRsvpStudent().setId(rsvpStudentId);
    }

    @JsonProperty("ticketStudentId")
    public BigInteger getTicketStudentId() {
        return getTicketStudent().getId();
    }

    public void setTicketStudentId(BigInteger ticketStudentId) {
        getTicketStudent().setId(ticketStudentId);
    }

    @JsonProperty("eventName")
    public String getEventName() {
        return getEvent().getName();
    }

    @JsonIgnore
    public Event getEvent() {
        if (event == null) {
            load();
        }
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @JsonIgnore
    public Student getRsvpStudent() {
        if (rsvpStudent == null) {
            load();
        }
        return rsvpStudent;
    }

    public void setRsvpStudent(Student rsvpStudent) {
        this.rsvpStudent = rsvpStudent;
    }

    @JsonIgnore
    public Student getTicketStudent() {
        System.out.println("ticketStudent get");
        if (ticketStudent == null) {
            load();
        }
        return ticketStudent;
    }

    public void setTicketStudent(Student ticketStudent) {
        System.out.println("ticketStudent set " + ticketStudent.getId());
        this.ticketStudent = ticketStudent;
    }

    @JsonProperty("specialPreferences")
    public String getSpecialPreferences() {
        System.out.println("specialPreferences get");
        if (specialPreferences == null) { // Check if not already loaded
            load();
        }
        return specialPreferences;
    }

    public void setSpecialPreferences(String specialPreferences) {
        System.out.println("specialPreferences set");
        if (specialPreferences == null) {
            this.specialPreferences = "";
        } else {
            this.specialPreferences = specialPreferences;
        }

    }
}
