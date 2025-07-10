interface FundingApplication {
  state: 'SubmittedState' | 'ApprovedState' | 'InReviewState' | 'RejectedState' | 'DraftState';
  id: number;
  clubID: number;
  description: string;
  amount: number;
  semester: number;
  year: string;
  submittedAt: number[];
}

export { FundingApplication };
