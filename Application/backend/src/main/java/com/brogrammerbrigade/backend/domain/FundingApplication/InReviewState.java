package com.brogrammerbrigade.backend.domain.FundingApplication;

public class InReviewState extends FundingApplicationState {
    public InReviewState(FundingApplicationContext context) { super(context); }

    @Override
    public void getReviews() {
        System.out.println("Getting reviews for application in review");
    }

    @Override
    public void addReview() {
        System.out.println("Adding review");
    }

    @Override
    public void approveApplication() {
        context.setState(new ApprovedState(context));
    }

    @Override
    public void rejectApplication() {
        context.setState(new RejectedState(context));
    }


    // To set the application as reviewable when a reviewer cancels review
    // This method is used to cancel the review. not to cancel the application.
    @Override
    public void cancelApplication() {
        context.setState(new SubmittedState(context));
    }
}
