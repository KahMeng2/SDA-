package com.brogrammerbrigade.backend.domain.FundingApplication;

import com.brogrammerbrigade.backend.datasource.FundingApplicationMapper;
import com.brogrammerbrigade.backend.domain.DomainObject;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Year;

public class FundingApplicationContext extends DomainObject {
    @JsonSerialize(using = FundingApplicationStateSerializer.class)
    private FundingApplicationState state;
    private BigInteger id;
    private BigInteger clubID;
    private String description;
    private Double amount;
    private Integer semester;
    private Year year;
    @JsonIgnore
    private FundingApplicationStatus status;
    private LocalDateTime submittedAt;

    // Empty constructor for Lock Manager
    public FundingApplicationContext(){}

    // Constructor for new applications
    public FundingApplicationContext(BigInteger clubID, Integer semester, Year year) {
        this.state = new DraftState(this);
        this.clubID = clubID;
        this.status = FundingApplicationStatus.IN_DRAFT;
        setSemester(semester);
        this.year = year;
    }

    // Constructor for existing applications
    public FundingApplicationContext(BigInteger id, BigInteger clubID, Integer semester, Year year, FundingApplicationStatus status) {
        this.id = id;
        this.clubID = clubID;
        this.semester = semester;
        this.year = year;
        setStatus(status);
        setStateFromStatus(status);
    }

    public FundingApplicationContext(BigInteger id) {
        this.id = id;
    }

    // Getters and Setters
    public FundingApplicationState getState() {
        return state;
    }

    public void setState(FundingApplicationState state) {
        this.state = state;
        if (state instanceof DraftState) {
            this.status = FundingApplicationStatus.IN_DRAFT;
        } else if (state instanceof SubmittedState) {
            this.status = FundingApplicationStatus.SUBMITTED;
        } else if (state instanceof InReviewState) {
            this.status = FundingApplicationStatus.IN_REVIEW;
        } else if (state instanceof ApprovedState) {
            this.status = FundingApplicationStatus.APPROVED;
        } else if (state instanceof RejectedState) {
            this.status = FundingApplicationStatus.REJECTED;
        } else if (state instanceof CancelledState) {
            this.status = FundingApplicationStatus.CANCELLED;
        }
        // Update state on the database
        // FundingApplicationMapper.getInstance().update(this);
    }

        @JsonIgnore
    public String getStatusValue() {
        return this.status.getValue();
    }

    public void setStateFromStatus(FundingApplicationStatus status) {
        switch (status) {
            case IN_DRAFT:
                this.state = new DraftState(this);
                break;
            case SUBMITTED:
                this.state = new SubmittedState(this);
                break;
            case IN_REVIEW:
                this.state = new InReviewState(this);
                break;
            case APPROVED:
                this.state = new ApprovedState(this);
                break;
            case REJECTED:
                this.state = new RejectedState(this);
                break;
            case CANCELLED:
                this.state = new CancelledState(this);
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    // State transition methods
    public void submitApplication() {
        state.submitApplication();
    }

    public void approveApplication() {
        state.approveApplication();
    }

    public void reviewApplication() {state.addReview();}

    public void rejectApplication() {
        state.rejectApplication();
    }

    public void cancelApplication() { state.cancelApplication(); }

    @JsonIgnore
    public boolean isEditable() {
        return (state instanceof DraftState) || (state instanceof SubmittedState);
    }

    // Other Getters and Setters

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public FundingApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(FundingApplicationStatus status) {
        this.status = status;
    }

    public BigInteger getClubID() {
        return clubID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getSemester() {
        return semester;
    }

    // Checks the given semester is a valid 1 or 2
    public void setSemester(Integer semester) {
        if (semester != 1 && semester != 2) {
            throw new VisibleException("Semester must be 1 or 2.", HttpServletResponse.SC_BAD_REQUEST);
        }
        this.semester = semester;
    }

    public Year getYear() {
        return year;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // Lazy load
    private void load() {
        // TODO implement lazy load for this class
    }
}
