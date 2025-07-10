import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '@/api/apiConfig';
import { FundingApplication } from '@/types/FundingApplication';
import { Badge, getFundingApplicationBadgeVariant } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { toast } from '@/hooks/use-toast';
import { Separator } from '@/components/ui/separator';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';

interface Club {
  id: number;
  name: string;
  balance: number;
}

interface PastApplication {
  id: number;
  submittedAt: number[];
  amount: number;
  state: string;
  semester: number;
  year: number;
}

const FundingApplicationDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [application, setApplication] = useState<FundingApplication | null>(null);
  const [club, setClub] = useState<Club | null>(null);
  const [pastApplications, setPastApplications] = useState<PastApplication[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false);
  const [reviewComments, setReviewComments] = useState('');
  const [isApproved, setIsApproved] = useState(false);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);

  const fetchData = async () => {
    if (!id) return;

    setLoading(true);
    setError(null);

    try {
      const appResponse = await axiosInstance.get<FundingApplication>(`/funding_application/${id}`);
      const application = appResponse.data;
      setApplication(application);

      const [clubResponse, pastAppsResponse] = await Promise.all([
        axiosInstance.get<Club>(`/clubs/${application.clubID}`),
        axiosInstance.get<PastApplication[]>(`/funding_application/club/${application.clubID}`),
      ]);

      setClub(clubResponse.data);
      setPastApplications(pastAppsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
      setError('Failed to load application details. Please try again later.');
      toast({
        title: 'Error',
        description: 'Failed to load application details',
        variant: 'destructive',
      });
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    fetchData();
  }, [id]);

  const handleOpenReviewModal = async () => {
    try {
      await axiosInstance.post(`/funding_application/${id}/review`);
      setIsReviewModalOpen(true);
    } catch (error) {
      console.error('Error acquiring lock:', error);
      toast({
        title: 'Error',
        description: 'Failed to start review process. Please try again.',
        variant: 'destructive',
      });
    }
  };

  const handleCloseReviewModal = useCallback(async () => {
    if (isReviewModalOpen) {
      try {
        await axiosInstance.put(`/funding_application/${id}/cancelReview`);
        setIsReviewModalOpen(false);
        setReviewComments('');
        setIsApproved(false);
      } catch (error) {
        console.error('Error releasing lock:', error);
        toast({
          title: 'Error',
          description: 'Failed to cancel review process. Please try again.',
          variant: 'destructive',
        });
      }
    }
  }, [id, isReviewModalOpen]);
  const handleSubmitReview = async () => {
    setIsSubmittingReview(true);
    try {
      await axiosInstance.put(`/funding_application/${id}/submitReview`, {
        decision: isApproved ? 'Approved' : 'Rejected',
        comments: reviewComments,
      });
      toast({
        title: 'Review Submitted',
        description: 'The funding application review has been successfully submitted.',
      });
      setIsReviewModalOpen(false);
      fetchData(); // Refresh the application details
    } catch (error) {
      console.error('Error submitting review:', error);
      toast({
        title: 'Submission Failed',
        description: 'Failed to submit the review. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsSubmittingReview(false);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!application || !club) {
    return <div>No data available</div>;
  }

  return (
    <div className="w-full mx-auto py-6 space-x-6 grid grid-cols-6">
      <Card className="col-span-4">
        <CardHeader>
          <CardTitle>Funding Application Details</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <p>
              <strong>Description:</strong> {application.description}
            </p>
            <p>
              <strong>Amount:</strong> ${application.amount.toFixed(2)}
            </p>
            <p>
              <strong>Semester:</strong> {application.semester}
            </p>
            <p>
              <strong>Year:</strong> {application.year}
            </p>
            <p>
              <strong>Submitted At:</strong>{' '}
              {new Date(
                application.submittedAt[0],
                application.submittedAt[1] - 1,
                application.submittedAt[2]
              ).toLocaleDateString()}
            </p>
            <p>
              <strong>Status:</strong>{' '}
              <Badge variant={getFundingApplicationBadgeVariant(application.state)}>
                {application.state.replace('State', '')}
              </Badge>
            </p>
          </div>
          <Separator className="my-4" />
          <div className="flex space-x-4">
            <Button onClick={handleOpenReviewModal}>Update Status</Button>
            <Dialog
              open={isReviewModalOpen}
              onOpenChange={(open) => {
                if (!open) {
                  handleCloseReviewModal();
                }
              }}
            >
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Review Funding Application</DialogTitle>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                  <div className="flex items-center space-x-2">
                    <Switch id="approved" checked={isApproved} onCheckedChange={setIsApproved} />
                    <Label htmlFor="approved">{isApproved ? 'Approved' : 'Rejected'}</Label>
                  </div>
                  <Textarea
                    placeholder="Enter your comments here"
                    value={reviewComments}
                    onChange={(e) => setReviewComments(e.target.value)}
                  />
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={handleCloseReviewModal}>
                    Cancel
                  </Button>
                  <Button onClick={handleSubmitReview} disabled={isSubmittingReview}>
                    {isSubmittingReview ? 'Submitting...' : 'Submit Review'}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        </CardContent>
      </Card>

      <Card className="col-span-2">
        <CardHeader>
          <CardTitle>Club Information</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <p>
              <strong>Name:</strong> {club.name}
            </p>
            <p>
              <strong>Current Balance:</strong> ${club.balance.toFixed(2)}
            </p>
          </div>
          <Separator className="my-4" />
          <h3 className="text-lg font-semibold mb-2">Past Applications</h3>
          <ul className="space-y-2">
            {pastApplications.map((app) => (
              <li key={app.id}>
                <p>
                  {app.year}, Semester {app.semester}: ${app.amount.toFixed(2)} -
                  <Badge variant={getFundingApplicationBadgeVariant(app.state)}>
                    {app.state.replace('State', '')}
                  </Badge>
                </p>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
};

export default FundingApplicationDetails;
