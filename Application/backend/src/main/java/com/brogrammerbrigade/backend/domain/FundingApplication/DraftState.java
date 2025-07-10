package com.brogrammerbrigade.backend.domain.FundingApplication;

public class DraftState extends FundingApplicationState {
    public DraftState(FundingApplicationContext context) {
        super(context);
    }

    @Override
    public void submitApplication() {
        System.out.println("Submitting application");
        context.setState(new SubmittedState(context));
    }

    @Override
    public void cancelApplication() {
        System.out.println("Cancelling application in Draft state");
        context.setState(new CancelledState(context));
    }
}
