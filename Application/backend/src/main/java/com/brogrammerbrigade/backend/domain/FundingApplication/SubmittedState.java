package com.brogrammerbrigade.backend.domain.FundingApplication;

public class SubmittedState extends FundingApplicationState {
    public SubmittedState(FundingApplicationContext context) { super(context); }

    @Override
    public void addReview() {
        context.setState(new InReviewState(context));
    }

    @Override
    public void approveApplication() {
        context.setState(new ApprovedState(context));
    }

    @Override
    public void rejectApplication() {
        context.setState(new RejectedState(context));
    }

    @Override
    public void cancelApplication() {
        context.setState(new CancelledState(context));
    }
}