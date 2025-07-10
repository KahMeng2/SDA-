package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Review extends DomainObject{
    private FacultyMember facultyMember;
    private FundingApplicationContext fundingApplicationContext;
    private LocalDateTime startDate;
    private ReviewDecision decision;
    private String comments;

    // Getters and setters
    @JsonProperty("facultyMemberId")
    public BigInteger getFacultyMemberId() {
        if (facultyMember == null) {
            return null;
        }
        return facultyMember.getId();
    }

    @JsonProperty("fundingApplicationId")
    public BigInteger getFundingApplicationId() {
        if (fundingApplicationContext == null) {
            return null;
        }
        return fundingApplicationContext.getId();
    }

    public void setFacultyMember(FacultyMember facultyMember) {
        this.facultyMember = facultyMember;
    }

    @JsonIgnore
    public FacultyMember getFacultyMember() {
        return this.facultyMember;
    }

    public void setFundingApplicationContext(FundingApplicationContext fundingApplicationContext) {
        this.fundingApplicationContext = fundingApplicationContext;
    }

    @JsonIgnore
    public FundingApplicationContext getFundingApplicationContext() {
        return this.fundingApplicationContext;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    public ReviewDecision getDecision() {
        return this.decision;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return this.comments;
    }

}
