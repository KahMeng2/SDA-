package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.*;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

public class Club extends DomainObject{
    private BigInteger id;
    private String name;
    private Double balance;
    private List<Student> admins;
    private List<Event> events;
    private List<FundingApplicationContext> fundingApplicationContexts;

    public Club() {}

    @JsonCreator
    public Club(BigInteger id) {
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
    public List<Student> getAdmins() {
        if (this.admins == null) {
            StudentMapper studentMapper = StudentMapper.getInstance();
            setAdmins(studentMapper.getAdminsForClub(this));
        }
        return this.admins;
    }

    public void setAdmins(List<Student> admins) {
        this.admins = admins;
    }

    @JsonIgnore
    public List<Event> getEvents() {
        if (this.events == null) {
            EventMapper eventMapper = EventMapper.getInstance();
            setEvents(eventMapper.getEventsForClub(this));
        }
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @JsonIgnore
    public List<FundingApplicationContext> getFundingApplications() {
        if (this.fundingApplicationContexts == null) {
            // TODO implement with fundingApplication mapper
            // FundingApplicationMapper fundingApplicationMapper = FundingApplicationMapper.getInstance();
            // setFundingApplications(fundingApplicationMapper.getFundingApplicationsForClub(this));
            return null;
        }
        return this.fundingApplicationContexts;
    }

    public void setFundingApplications(List<FundingApplicationContext> fundingApplicationContexts) {
        this.fundingApplicationContexts = fundingApplicationContexts;
    }

    private void load() {
        ClubMapper clubMapper = ClubMapper.getInstance();
        Club clubData = clubMapper.getClubById(id);
        if (name == null) {
            setName(clubData.getName());
        }

        if (balance == null) {
            setBalance(clubData.getBalance());
        }

    }

    @JsonProperty("name")
    public String getName() {
        if (name == null) {
            load();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("balance")
    public Double getBalance() {
        if (balance == null) {
            load();
        }
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean exists(){
        ClubMapper clubMapper = ClubMapper.getInstance();
        Club clubData = clubMapper.getClubById(id);
        return clubData != null;
    }
}
