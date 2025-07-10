package com.brogrammerbrigade.backend.domain.FundingApplication;

public abstract class FundingApplicationState {
    protected FundingApplicationContext context;

    public FundingApplicationState(FundingApplicationContext context) {
        this.context = context;
    }

//    public void getAllActiveApplications() {
//        // TODO migrate the function to FundingApplicationState Objects
//    }

    public void submitApplication() {
        throw new UnsupportedOperationException("This state does not support submitting applications.");
    }

    public void addReview() {
        throw new UnsupportedOperationException("This state does not support adding reviews.");
    }

    public void approveApplication() {
        throw new UnsupportedOperationException("This state does not support approving applications.");
    }

    public void rejectApplication() {
        throw new UnsupportedOperationException("This state does not support rejecting applications.");
    }

    public void cancelApplication() {
        throw new UnsupportedOperationException("This state does not support cancelling applications.");
    }

    public void cancelReview(){
        throw new UnsupportedOperationException("This state does not support cancelling reviews.");
    }


    public void getReviews() {
        throw new UnsupportedOperationException("This state does not support getting reviews.");
    }

}
