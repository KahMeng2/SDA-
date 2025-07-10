package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.ClubMapper;
import com.brogrammerbrigade.backend.datasource.FacultyMemberMapper;
import com.brogrammerbrigade.backend.datasource.FundingApplicationMapper;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.domain.FundingApplication.SubmittedState;
import com.brogrammerbrigade.backend.domain.Review;
import com.brogrammerbrigade.backend.datasource.ReviewMapper;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationStatus;
import com.brogrammerbrigade.backend.domain.ReviewDecision;
import com.brogrammerbrigade.backend.port.postgres.LockManager;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FundingApplicationService {
    private static FundingApplicationService instance;
    private final FundingApplicationMapper fundingApplicationMapper;
    private final ReviewMapper reviewMapper = ReviewMapper.getInstance();
    private final FacultyMemberMapper facultyMemberMapper = FacultyMemberMapper.getInstance();
    private final ClubMapper clubMapper = ClubMapper.getInstance();
    private final LockManager lockManager = LockManager.getInstance();


    private FundingApplicationService() { this.fundingApplicationMapper = FundingApplicationMapper.getInstance(); }

    // Get the singleton instance
    public static synchronized FundingApplicationService getInstance() {
        if (instance == null) {
            instance = new FundingApplicationService();
        }
        return instance;
    }

    // Gets all the Funding Applications from the database
    public ArrayList<FundingApplicationContext> getAllFundingApplications() {
        return fundingApplicationMapper.getAllFundingApplications();
    }

    // Finds a specific funding application by ID
    public FundingApplicationContext getFundingApplicationById(BigInteger id) {
        return fundingApplicationMapper.getFundingApplicationById(id);
    }

    public List<FundingApplicationContext> getFundingApplicationsByClubId(BigInteger clubID) {
        return fundingApplicationMapper.getFundingApplicationsByClubId(clubID);
    }

    // Create funding application with given information
    public FundingApplicationContext createFundingApplication(BigInteger clubID, String description, Double amount, Integer semester, Year year) {
        // Check the year is current or in the future
        if (year.isBefore(Year.now())) {
            throw new IllegalArgumentException("Cannot create an application for a past year.");
        }

        // Check if the club has already created an application for this semester and year.
        if (fundingApplicationMapper.existsApplicationForClubAndSem(clubID, semester, year)) {
            throw new IllegalArgumentException("Club already has a funding application for this semester.");
        }

        FundingApplicationContext application = new FundingApplicationContext(clubID, semester, year);
        application.setDescription(description);
        application.setAmount(amount);
        application.setStatus(FundingApplicationStatus.IN_DRAFT);
        return fundingApplicationMapper.insert(application);
    }

    // Update data for an existing funding application
    public FundingApplicationContext updateFundingApplication(FundingApplicationContext application) {
        fundingApplicationMapper.update(application);
        return application;
    }

    // Submit a funding application and change state from in draft to submitted
    public void submitApplication(BigInteger applicationID) {
        FundingApplicationContext application = getFundingApplicationById(applicationID);
        if (application != null) {
            application.submitApplication();
            updateFundingApplication(application);
        } else {
            throw new RuntimeException("Funding application not found with ID: " + applicationID);
        }
    }
    public void startEditFundingApplication(BigInteger applicationID, String username) {
        FundingApplicationContext application = getFundingApplicationById(applicationID);
        // Check the application exists
        if (application == null) {
            throw new IllegalArgumentException("Funding application not found with ID: " + applicationID);
        }

        // Check if the application is in a state that can be edited
        if (!application.isEditable()) {
            throw new IllegalStateException("Funding application can only be edited in 'In Draft' or 'Submitted' state. Current state: " + application.getState().getClass().getSimpleName());
        }

        // Acquires lock for funding app
        lockManager.acquireLock(new FundingApplicationContext(), applicationID.toString(), username);
    }

    public void stopEditFundingApplication(BigInteger applicationID, String username) {
        // Acquires lock for funding app
        lockManager.releaseLock(new FundingApplicationContext(), applicationID.toString(), username);
    }

    // Cancel a funding application, transitioning the state to cancelled
    public void cancelApplication(BigInteger applicationID, String username) {
        lockManager.isLockValid(new FundingApplicationContext(), applicationID.toString(), username);
        FundingApplicationContext application = getFundingApplicationById(applicationID);
        if (application != null) {
            application.cancelApplication();
            updateFundingApplication(application);
        } else {
            throw new RuntimeException("Funding application not found with ID: " + applicationID);
        }
        lockManager.releaseLock(new FundingApplicationContext(), applicationID.toString(), username);
    }

    // Edit an existing funding application
    public FundingApplicationContext commitEditFundingApplication(BigInteger applicationID, Map<String, Object> updates, String username) {
        FundingApplicationContext application = getFundingApplicationById(applicationID);

        try{
            // Checks if user has lock to commit edit.
            lockManager.isLockValid(new FundingApplicationContext(), applicationID.toString(), username);

            // Check the application exists
            if (application == null) {
                throw new IllegalArgumentException("Funding application not found with ID: " + applicationID);
            }

            // Check if the application is in a state that can be edited
            if (!application.isEditable()) {
                throw new IllegalStateException("Funding application can only be edited in 'In Draft' or 'Submitted' state. Current state: " + application.getState().getClass().getSimpleName());
            }

            if (updates.containsKey("description")) {
                application.setDescription((String) updates.get("description"));
            }
            if (updates.containsKey("amount")) {
                application.setAmount(Double.valueOf((String) updates.get("amount")));
            }

            return fundingApplicationMapper.update(application);
        }finally {
            lockManager.releaseLock(new FundingApplicationContext(), applicationID.toString(), username);
        }
    }

    // Delete a funding application from the database (should use cancel instead when it is implemented)
    public void deleteFundingApplication(BigInteger applicationID) {
        lockManager.acquireLock(new FundingApplicationContext(), applicationID.toString(), Thread.currentThread().getName());
        fundingApplicationMapper.delete(new FundingApplicationContext(applicationID));
        lockManager.releaseLock(new FundingApplicationContext(), applicationID.toString(), Thread.currentThread().getName());
    }

    // Create a review for funding application
    public void startReview(BigInteger applicationId, BigInteger facultyMemberId) {
        FundingApplicationContext application = getFundingApplicationById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Funding application not found with ID: " + applicationId);
        }

        // Ensure the application is in a submitted state
        if (application.getStatus() != FundingApplicationStatus.SUBMITTED) {
            throw new IllegalStateException("Reviews can only be created for submitted applications.");
        }

        // Check faculty member exists
        FacultyMember facultyMember = facultyMemberMapper.getFacultyMemberById(facultyMemberId);
        if (facultyMember == null) {
            throw new IllegalArgumentException("Faculty member not found with ID: " + facultyMemberId);
        }

        // Check if a review already exists for this application
        if (reviewMapper.getReview(facultyMember, application) != null) {
            throw new IllegalStateException("A review already exists for this application.");
        }

        // lock stuff
        lockManager.acquireLock(new Review(), applicationId.toString(), facultyMemberId.toString());

        // Sets application to be in review
        application.reviewApplication();

        // Updates state of funding application
        fundingApplicationMapper.update(application);

    }
    // Stop a review
    public void stopReview(BigInteger applicationId, BigInteger facultyMemberId) {
        FundingApplicationContext application = getFundingApplicationById(applicationId);
        // Ensure the application is in a submitted state
        if (application.getStatus() != FundingApplicationStatus.IN_REVIEW) {
            throw new IllegalStateException("Reviews can only be stopped for in review applications.");
        }
        // NOTE: That when application is in the state of in Review. cancelApplication sets the state to submitted again.
        application.cancelApplication();
        // Updates state of funding app
        fundingApplicationMapper.update(application);

        lockManager.releaseLock(new Review(), applicationId.toString(), facultyMemberId.toString());
    }

    // Add a review to a funding application and change the state to in review;
    public Review submitReview(BigInteger applicationId, BigInteger facultyMemberId, Map<String, Object> reviewData) {

        try {
            FundingApplicationContext application = getFundingApplicationById(applicationId);
            if (application == null) {
                throw new IllegalArgumentException("Funding application not found with ID: " + applicationId);
            }

            // Ensure the application is in a submitted state
            if (application.getStatus() != FundingApplicationStatus.IN_REVIEW) {
                throw new IllegalStateException("Reviews can only be submitted for in review applications.");
            }

            // Check faculty member exists
            FacultyMember facultyMember = facultyMemberMapper.getFacultyMemberById(facultyMemberId);
            if (facultyMember == null) {
                throw new IllegalArgumentException("Faculty member not found with ID: " + facultyMemberId);
            }

            // Check if a review already exists for this application
            if (reviewMapper.getReview(facultyMember, application) != null) {
                throw new IllegalStateException("A review already exists for this application.");
            }
            System.out.println("Checks valid");

            // Checks if lock is hold by user
            lockManager.isLockValid(new Review(), applicationId.toString(), facultyMemberId.toString());

            // Check decision is valid
            String decisionString = (String) reviewData.get("decision");
            ReviewDecision decision;
            try {
                decision = ReviewDecision.valueOf(decisionString);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid decision: " + decisionString);
            }

            String comments = (String) reviewData.get("comments");
            System.out.println("Creating review for funding app");
            // Create and set a new Review object
            Review review = new Review();
            review.setFundingApplicationContext(application);
            review.setFacultyMember(facultyMember);
            review.setStartDate(LocalDateTime.now());
            review.setDecision(decision);
            review.setComments(comments);

            reviewMapper.insert(review);

            // Update the application state based on the review decision
            if (decision == ReviewDecision.Approved) {
                application.approveApplication();

                // Update club balance
                Double newBalance = application.getAmount();
                BigInteger clubID = application.getClubID();
                Club studentClub = clubMapper.getClubById(clubID);
                Double originalBalance = studentClub.getBalance();
                studentClub.setBalance(newBalance + originalBalance);

                // Update on database
                clubMapper.update(studentClub);

            } else if (decision == ReviewDecision.Rejected) {
                application.rejectApplication();
            }
            fundingApplicationMapper.update(application);

            return review;
        } finally {
            lockManager.releaseLock(new Review(), applicationId.toString(), facultyMemberId.toString());
        }
    }

    // Approve a funding application, transitioning the state to approved
    public void approveApplication(BigInteger applicationID) {
        FundingApplicationContext application = getFundingApplicationById(applicationID);
        if (application != null) {
            application.approveApplication();
            updateFundingApplication(application);
        } else {
            throw new RuntimeException("Funding application not found with ID: " + applicationID);
        }
    }

    // Reject a funding application, transitioning the state to rejected
    public void rejectApplication(BigInteger applicationID) {
        FundingApplicationContext application = getFundingApplicationById(applicationID);
        if (application != null) {
            application.rejectApplication();
            updateFundingApplication(application);
        } else {
            throw new RuntimeException("Funding application not found with ID: " + applicationID);
        }
    }



    // Get all reviews
    public List<Review> getAllReviews() {
        return reviewMapper.getAllReviews();
    }

    // Get reviews associated with a Funding Application
    public List<Review> getReviewsForApplication(FundingApplicationContext application) {
        return reviewMapper.getReviewsForFundingApplication(application);
    }
    public BigInteger getClubIdForFundingApplication(BigInteger applicationId) {
        FundingApplicationContext application = getFundingApplicationById(applicationId);
        if (application == null) {
            throw new RuntimeException("Funding application not found with ID: " + applicationId);
        }
        return application.getClubID();
    }

}