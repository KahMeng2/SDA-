import React, { useState, useEffect, useCallback } from 'react';
import axiosInstance from '../../api/apiConfig';
import { Separator } from '@/components/ui/separator';
import ClubOverview from '@/components/ClubOverview';
import CreateClubDialog from '@/components/CreateClubDialog';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { Card } from '@/components/ui/card';

interface Club {
  id: string;
  name: string;
  balance: number;
  createdAt: string | null;
  admin: string;
}

export default function Clubs() {
  const [clubs, setClubs] = useState<Club[]>([]);
  const [loaded, setLoaded] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // const fetchClubs = useCallback(async () => {
  //   try {
  //     const response = await axiosInstance.get<Club[]>('/clubs');
  //     setClubs(response.data);
  //     setLoaded(true);
  //   } catch (e) {
  //     if (e instanceof Error) {
  //       setError(`Oops! We encountered an error while fetching the clubs: ${e.message}`);
  //     }
  //   }
  // }, []);
  const fetchClubs = useCallback(
    async (
      setClubs: React.Dispatch<React.SetStateAction<Club[]>>,
      setLoaded: React.Dispatch<React.SetStateAction<boolean>>,
      setError: React.Dispatch<React.SetStateAction<string | null>>
    ) => {
      try {
        const response = await axiosInstance.get<Club[]>('/clubs');
        setClubs(response.data);
        setLoaded(true);
      } catch (e) {
        if (axios.isAxiosError(e)) {
          setError(`Oops! We encountered an error while fetching the clubs: ${e.message}`);
        } else {
          setError('An unknown error occurred');
        }
      }
    },
    []
  );

  useEffect(() => {
    fetchClubs(setClubs, setLoaded, setError);
  }, [fetchClubs]);

  const handleClubCreated = () => {
    fetchClubs(setClubs, setLoaded, setError);
  };

  if (!loaded) return <div>Loading Clubs...</div>;

  if (error) return <div>{error}</div>;

  return (
    <div className="mx-auto grid flex-1 auto-rows-max gap-4 pb-6">
      <div className="flex gap-4 justify-between pb-2 pt-4">
        <h1 className="flex-1 shrink-0 whitespace-nowrap text-3xl font-semibold tracking-tight sm:grow-0">
          Clubs
        </h1>
        <CreateClubDialog onClubCreated={handleClubCreated}>
          <Button variant="outline">Create</Button>
        </CreateClubDialog>
      </div>
      <Separator />

      <div className="grid gap-4">
        <Card className="grid auto-rows-max items-start gap-4 lg:col-span-2 lg:gap-8 py-4">
          {clubs.length === 0 ? (
            <p>No clubs available.</p>
          ) : (
            <ul className="space-y-4">
              {clubs.map((club) => (
                <li key={club.id}>
                  <ClubOverview club={club} />
                </li>
              ))}
            </ul>
          )}
        </Card>
      </div>
    </div>
  );
}
