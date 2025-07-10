import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { useNavigate } from 'react-router-dom';
import { Event } from '@/types/Event';
import { Edit, XCircle } from 'lucide-react';

interface EventsTableProps {
  events: Event[];
  onEditEvent?: (event: Event) => void;
  onCancelEvent?: (eventId: number) => void;
  showActions?: boolean;
}

function EventsTable({
  events,
  onEditEvent,
  onCancelEvent,
  showActions = false,
}: EventsTableProps) {
  const navigate = useNavigate();

  const handleRowClick = (event: Event) => {
    navigate(`/events/${event.id}`, { state: { event } });
  };

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Event Name</TableHead>
          <TableHead>Start Time</TableHead>
          <TableHead>Status</TableHead>
          <TableHead>RSVPs</TableHead>
          {showActions && <TableHead>Actions</TableHead>}
        </TableRow>
      </TableHeader>
      <TableBody>
        {events.map((event: Event) => (
          <TableRow key={event.id} className="cursor-pointer hover:bg-gray-100">
            <TableCell onClick={() => handleRowClick(event)}>
              <div className="font-bold">{event.name}</div>
            </TableCell>
            <TableCell onClick={() => handleRowClick(event)}>
              {new Date(event.startTime).toLocaleString()}
            </TableCell>
            <TableCell onClick={() => handleRowClick(event)}>
              <Badge className="text-xs" variant={event.isCancelled ? 'destructive' : 'outline'}>
                {event.isCancelled ? 'Cancelled' : event.isOnline ? 'Online' : 'In-person'}
              </Badge>
            </TableCell>
            <TableCell onClick={() => handleRowClick(event)}>
              {event.numTickets} / {event.capacity}
            </TableCell>
            {showActions && (
              <TableCell>
                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      onEditEvent && onEditEvent(event);
                    }}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    Edit
                  </Button>
                  {!event.isCancelled && (
                    <Button
                      variant="destructive"
                      size="sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        onCancelEvent && onCancelEvent(event.id);
                      }}
                    >
                      <XCircle className="h-4 w-4 mr-1" />
                      Cancel
                    </Button>
                  )}
                </div>
              </TableCell>
            )}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

export default EventsTable;
