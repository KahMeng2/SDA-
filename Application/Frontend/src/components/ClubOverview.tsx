import React from 'react';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Club } from '@/types/Clubs';
import getInitials from '@/utils/getInitials';
import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

interface ClubOverviewProps {
  club: Club;
}
const ClubOverview: React.FC<ClubOverviewProps> = ({ club }) => {
  const initials = getInitials(club.name);
  return (
    <Link to={`/clubs/${club.id.toString()}`} key={club.id}>
      <div className="flex items-center justify-between gap-4 hover:bg-gray-50 h-full py-2 cursor-pointer">
        <div className="flex items-center gap-4 mx-4">
          <Avatar className="hidden h-9 w-9 sm:flex">
            <AvatarFallback>{initials}</AvatarFallback>
          </Avatar>
          <div className="grid gap-1 content-end">
            <p className="text-md font-semibold leading-none">{club.name}</p>
          </div>
        </div>
        <ChevronRight className="h-5 w-5 text-gray-400 mr-4" />
      </div>
    </Link>
  );
};

export default ClubOverview;
