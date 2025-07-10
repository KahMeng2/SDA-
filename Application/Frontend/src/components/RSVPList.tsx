import React from 'react';
import { Button } from '@/components/ui/button';
import { X } from 'lucide-react';

interface Ticket {
  specialPreferences: string;
  eventName: string;
  rsvpStudentId: number;
  eventId: number;
  ticketStudentId: number;
}

interface RSVP {
  tickets: Ticket[];
  dateCreated: string;
  rsvpStudentId: number;
  eventId: number;
}

interface RSVPListProps {
  rsvps: RSVP[];
  onRemoveRSVP: (rsvpStudentId: number, eventId: number) => Promise<void>;
}

const RSVPList: React.FC<RSVPListProps> = ({ rsvps, onRemoveRSVP }) => {
  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold">RSVPs</h3>
      {rsvps.map((rsvp) => (
        <div key={rsvp.rsvpStudentId} className="border p-4 rounded-md">
          <div className="flex justify-between items-center">
            <span className="font-medium">Student ID: {rsvp.rsvpStudentId}</span>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onRemoveRSVP(rsvp.rsvpStudentId, rsvp.eventId)}
            >
              <X className="h-4 w-4" />
            </Button>
          </div>
          {rsvp.tickets.length > 0 && (
            <ul className="mt-2 space-y-1">
              {rsvp.tickets.map((ticket) => (
                <li key={ticket.ticketStudentId} className="ml-4">
                  - Guest: {ticket.ticketStudentId}
                  {ticket.specialPreferences && ` (${ticket.specialPreferences})`}
                </li>
              ))}
            </ul>
          )}
        </div>
      ))}
    </div>
  );
};

export default RSVPList;
