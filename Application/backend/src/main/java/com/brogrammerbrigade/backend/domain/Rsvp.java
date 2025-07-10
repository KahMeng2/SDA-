package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.RsvpMapper;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.datasource.TicketMapper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;

public class Rsvp extends DomainObject {
    private Student rsvpStudent;
    private Event event;
    private List<Ticket> tickets;
    private OffsetDateTime dateCreated;

    public Rsvp() {}

    // foreign key objects are instantiated at class creation
    public Rsvp(BigInteger studentId, BigInteger eventId) {
        this.setRsvpStudent(new Student(studentId));
        this.setEvent(new Event(eventId));
    }

    // ghost lazy load for all attributes except lists
    private void load() {
        RsvpMapper rsvpMapper = RsvpMapper.getInstance();
        Rsvp rsvpData = rsvpMapper.getRsvp(rsvpStudent, event);

        if (rsvpStudent == null) {
            setRsvpStudent(rsvpData.getRsvpStudent());
        }

        if (event == null) {
            setEvent(rsvpData.getEvent());
        }

        if (dateCreated == null) {
            setDateCreated(rsvpData.getDateCreated());
        }
    }

    // Foreign key attributes do not need lazy loading
    @JsonIgnore
    public Student getRsvpStudent() {
        if (this.rsvpStudent == null) {
            load();
        }
        return rsvpStudent;
    }

    public void setRsvpStudent(Student student) {
        this.rsvpStudent = student;
    }

    @JsonIgnore
    public Event getEvent() {
        if (this.event == null) {
            load();
        }
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    // Lists of related objects are loaded only when needed
    @JsonProperty("tickets")
    public List<Ticket> getTickets() {
        if (this.tickets == null) {
            TicketMapper ticketMapper = TicketMapper.getInstance();
            setTickets(ticketMapper.getTicketsForRsvp(this));
        }
        return this.tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    // the only non-key non list attribute in this class
    @JsonProperty("dateCreated")
    public OffsetDateTime getDateCreated() {
        if (this.dateCreated == null) {
            load();
        }
        return this.dateCreated;
    }

    public void setDateCreated(OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    // getters and setters for foreign keys for convenience
    @JsonProperty("rsvpStudentId")
    public BigInteger getRsvpStudentId() {
        if (rsvpStudent == null) {
            load();
        }
        return this.rsvpStudent.getId();
    }

    public void setRsvpStudentId(BigInteger rsvpStudentId) {
        if (this.rsvpStudent == null) {
            this.rsvpStudent = new Student(rsvpStudentId);
        } else {
            this.rsvpStudent.setId(rsvpStudentId);
        }
    }

    @JsonProperty("eventId")
    public BigInteger getEventId() {
        if (event == null) {
            load();
        }
        return this.event.getId();
    }

    public void setEventId(BigInteger eventId) {
        if (this.event == null) {
            this.event = new Event(eventId);
        } else {
            this.event.setId(eventId);
        }
    }
}
