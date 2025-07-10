import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';
import axiosInstance from '@/api/apiConfig';
import { FundingApplication } from '@/types/FundingApplication';
import FundingApplicationsTable from '@/components/FundingApplicationsTable';

export default function Faculty() {
  const [applications, setApplications] = useState<FundingApplication[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFundingApplications = async () => {
      try {
        setLoading(true);

        const response = await axiosInstance.get<FundingApplication[]>('/funding_application');
        const nonDraftApplications = response.data.filter((app) => app.state !== 'DraftState');
        setApplications(nonDraftApplications);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch funding applications:', err);
        setError('Failed to load funding applications. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchFundingApplications();
  }, []);

  return (
    <div className="container mx-auto py-6">
      <h1 className="text-3xl font-semibold tracking-tight mb-4">Faculty Dashboard</h1>
      <Card>
        <CardHeader>
          <CardTitle>Funding Applications</CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <Skeleton className="h-48 w-full" />
          ) : error ? (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>Error</AlertTitle>
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          ) : (
            <FundingApplicationsTable applications={applications} />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
