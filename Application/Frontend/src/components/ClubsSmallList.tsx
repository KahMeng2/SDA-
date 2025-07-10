import { useState, useEffect } from 'react';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Club } from '@/types/Clubs';
import getInitials from '@/utils/getInitials';
import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import axiosInstance from '@/api/apiConfig';
import { Skeleton } from '@/components/ui/skeleton';

function ClubsSmallList() {
  const [clubs, setClubs] = useState<Club[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchClubs = async () => {
      try {
        const response = await axiosInstance.get<Club[]>('/clubs');
        setClubs(response.data.slice(0, 9)); // Limit to 8 clubs
        setError(null);
      } catch (err) {
        console.error('Failed to fetch clubs:', err);
        setError('Failed to load clubs. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchClubs();
  }, []);

  if (loading) {
    return (
      <>
        {[...Array(3)].map((_, index) => (
          <Skeleton key={index} className="h-12 w-full mb-2" />
        ))}
      </>
    );
  }

  if (error) {
    return <p className="text-red-500">{error}</p>;
  }

  return (
    <>
      {clubs.map((club) => {
        const initials = getInitials(club.name);
        return (
          <Link to={`/clubs/${club.id}`} key={club.id}>
            <div className="flex items-center justify-between gap-4 hover:bg-gray-50 h-full py-2 cursor-pointer">
              <div className="flex items-center gap-4">
                <Avatar className="hidden h-9 w-9 sm:flex">
                  <AvatarFallback>{initials}</AvatarFallback>
                </Avatar>
                <div className="grid gap-1 content-end">
                  <p className="text-md font-semibold leading-none">{club.name}</p>
                </div>
              </div>
              <ChevronRight className="h-5 w-5 text-gray-400" />
            </div>
          </Link>
        );
      })}
    </>
  );
}

export default ClubsSmallList;
