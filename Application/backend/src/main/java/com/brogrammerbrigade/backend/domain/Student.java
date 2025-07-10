package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.ClubMapper;
import com.brogrammerbrigade.backend.datasource.RsvpMapper;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.datasource.TicketMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class Student extends User {
    private BigInteger studentId;
    @JsonProperty("administratedClubs")
    private List<Club> administrates;
    private List<Rsvp> rsvps;
    private List<Ticket> tickets;
    public Student() {
        setRole("STUDENT");
    }


    public Student(BigInteger id) {
        setId(id);
    }

    // ghost load
    @Override
    protected void load() {
        StudentMapper studentMapper = StudentMapper.getInstance();
        Student studentData = studentMapper.getUser(new Student(getId()));
        System.out.println("Student load");

        super.loadParent(studentData);
    }

    // Getters and setters

    @JsonProperty("administratedClubs")
    public List<Club> getAdministratedClubs() {
        if (administrates == null) {
            ClubMapper clubMapper = ClubMapper.getInstance();
            administrates = clubMapper.getAdminClubsForStudent(new Student(getId()));
        }
        return administrates;
    }

    public void setAdministratedClubs(List<Club> administrates) {
        this.administrates = administrates;
    }


    public void addAdministratedClub(Club club) {
        getAdministratedClubs().add(club);
    }

    public void removeAdministratedClub(Club club) {getAdministratedClubs().remove(club);}

    @JsonIgnore
    public List<Rsvp> getRsvps() {
        if (this.rsvps == null) {
            // TODO implement with rsvp mapper
            //this.rsvps = rsvpMapper.getRvpsByStudent(this);
            return null;
        }
        return this.rsvps;
    }

    public void setRsvps(List<Rsvp> rsvps) {
        this.rsvps = rsvps;

    }

    @JsonIgnore
    public List<Ticket> getTickets() {
        if (this.tickets == null) {
            TicketMapper ticketMapper = TicketMapper.getInstance();
            this.tickets = ticketMapper.getTicketsForStudent(this);
        }
        return this.tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;

    }

    @Override
    public List<String> getAuthorities() {
        List<String> authorities = super.getAuthorities();
        for (Club club : getAdministratedClubs()) {
            authorities.add("CLUB_ADMIN_" + club.getId());
        }
        return authorities;
    }
}
