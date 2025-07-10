import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '@/api/apiConfig';
import { Event } from '@/types/Event';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Calendar,
  ChevronLeft,
  Clock,
  // CogIcon,
  DollarSign,
  Info,
  LucideArrowUpRight,
  PersonStanding,
  Ticket,
  XCircle,
} from 'lucide-react';
import formatDate from '@/utils/formatDate';
import RSVPDialog from '@/components/RSVPDialog';
// import EditEventDialog from '@/components/EditEventDialog';
import DeleteConfirmationModal from '@/components/DeleteConfirmationModal';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { toast } from '@/hooks/use-toast';
import { useAuth } from '@/hooks/AuthProvider';
import axios from 'axios';

function EventDetails() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [event, setEvent] = useState<Event | null>(null);
  const [rsvps, setRSVPs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  const fetchEventDetails = async () => {
    try {
      setLoading(true);
      const [eventResponse, rsvpResponse] = await Promise.all([
        axiosInstance.get<Event>(`/events/${id}`),
        axiosInstance.get(`/rsvps/forEvent?eventId=${id}`),
      ]);
      setEvent(eventResponse.data);
      setRSVPs(rsvpResponse.data);
      setError(null);
    } catch (error) {
      console.error('Error fetching event details:', error);
      setError('Failed to load event details. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEventDetails();
  }, [id]);

  // const handleSave = async (updatedEvent: Event) => {
  //   try {
  //     await axiosInstance.put(`/events/${id}`, updatedEvent);
  //     setEvent(updatedEvent);
  //     toast({
  //       title: 'Event Updated',
  //       description: 'The event details have been successfully updated.',
  //     });
  //   } catch (error) {
  //     console.error('Error updating event:', error);
  //     toast({
  //       title: 'Update Failed',
  //       description: 'There was an error updating the event. Please try again.',
  //       variant: 'destructive',
  //     });
  //   }
  // };

  const handleCancel = async () => {
    try {
      await axiosInstance.put(`/events/cancel?eventId=${id}`);
      // Refresh the event details to reflect the cancellation
      await fetchEventDetails();
      toast({
        title: 'Event Cancelled',
        description: 'The event has been successfully cancelled.',
      });
    } catch (error) {
      console.error('Error cancelling event:', error);
      if (axios.isAxiosError(error) && error.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to cancel this event.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'Cancellation Failed',
          description: 'There was an error cancelling the event. Please try again.',
          variant: 'destructive',
        });
      }
    }
  };

  const handleRemoveRSVP = async (rsvpStudentId: number, eventId: number) => {
    try {
      await axiosInstance.delete('/rsvps', {
        data: { rsvpStudentId: rsvpStudentId.toString(), eventId: eventId.toString() },
      });
      setRSVPs(rsvps.filter((rsvp) => rsvp.rsvpStudentId !== rsvpStudentId));
      toast({
        title: 'RSVP Removed',
        description: 'The RSVP has been successfully removed.',
      });
    } catch (error) {
      console.error('Error removing RSVP:', error);
      if (axios.isAxiosError(error) && error.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to remove this RSVP.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'RSVP Removal Failed',
          description: 'There was an error removing the RSVP. Please try again.',
          variant: 'destructive',
        });
      }
    }
  };
  if (loading) {
    return <Skeleton className="w-full h-96" />;
  }

  if (error) {
    return (
      <Alert variant="destructive">
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  if (!event) {
    return <div>Event not found</div>;
  }

  const userRSVP = rsvps.find((rsvp) => rsvp.rsvpStudentId === user?.id);

  return (
    <div className="flex flex-col w-full gap-2 pb-8">
      <header className="flex justify-between items-center">
        <div className="flex gap-4 justify-start pb-4 pt-4 items-center">
          <Button
            variant="outline"
            size="icon"
            className="h-7 w-7"
            onClick={() => navigate('/events')}
          >
            <ChevronLeft className="h-4 w-4" />
            <span className="sr-only">Back</span>
          </Button>
          <h1 className="flex-1 shrink-0 whitespace-nowrap text-3xl font-semibold tracking-tight sm:grow-0">
            {event.name}
          </h1>
        </div>
        <div className="flex flex-row gap-4">
          {/* <EditEventDialog event={event} onSave={handleSave}>
            <Button className="flex gap-2">
              <CogIcon className="h-4 w-4" />
              Edit
            </Button>
          </EditEventDialog> */}
          <DeleteConfirmationModal eventName={event.name} onDelete={handleCancel}>
            <Button className="flex gap-2 bg-rose-500 hover:bg-rose-600">
              <XCircle className="h-4 w-4" />
              Cancel
            </Button>
          </DeleteConfirmationModal>
        </div>
      </header>

      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Start Time</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatDate(event.startTime)}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">RSVPs</CardTitle>
            <PersonStanding className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{event.numTickets}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Status</CardTitle>
            <Info className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {event.isCancelled ? 'Cancelled' : event.isOnline ? 'Online' : 'In-person'}
            </div>
          </CardContent>
        </Card>
        {userRSVP ? (
          <RSVPDialog
            eventName={event.name}
            eventId={event.id.toString()}
            existingRSVP={userRSVP}
            onRSVPSuccess={fetchEventDetails}
            onDelete={() => handleRemoveRSVP(userRSVP.rsvpStudentId, event.id)}
          >
            <Button className="h-full bg-slate-800 hover:bg-slate-700 text-4xl font-bold gap-6 justify-items-center text-center flex">
              Edit <Ticket className="h-10 w-10 mt-1.5" />
            </Button>
          </RSVPDialog>
        ) : (
          <RSVPDialog
            eventName={event.name}
            eventId={event.id.toString()}
            onRSVPSuccess={fetchEventDetails}
            onDelete={() => handleRemoveRSVP(userRSVP.rsvpStudentId, event.id)}
          >
            <Button className="h-full  text-4xl font-bold  gap-6">
              RSVP <LucideArrowUpRight className="h-10 w-10" />
            </Button>
          </RSVPDialog>
        )}

        <Card className="col-span-1 md:col-span-3">
          <CardHeader>
            <CardTitle>Event Details</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div>
                <h3 className="font-semibold flex items-center text-center gap-2 py-1">
                  <Calendar className="h-4 w-4" /> Date and Time
                </h3>
                <p>
                  {formatDate(event.startTime)} - {formatDate(event.endTime)}
                </p>
              </div>
              <div>
                <h3 className="font-semibold flex items-center text-center gap-2 py-1">
                  <Info className="h-4 w-4" /> Description
                </h3>
                <p>{event.description}</p>
              </div>
              <div>
                <h3 className="font-semibold flex items-center text-center gap-2 py-1">
                  <DollarSign className="h-4 w-4" /> Cost
                </h3>
                <p>${event.cost}</p>
              </div>
              {event.clubName && (
                <div>
                  <h3 className="font-semibold flex items-center text-center gap-2 py-1">
                    <Info className="h-4 w-4" /> Organized by
                  </h3>
                  <p>{event.clubName}</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        <Card className="col-span-1">
          <CardHeader>
            <CardTitle>RSVPs</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {rsvps.map((rsvp) => (
                <RSVPDialog
                  key={rsvp.rsvpStudentId}
                  eventName={event.name}
                  eventId={event.id.toString()}
                  existingRSVP={rsvp}
                  onRSVPSuccess={fetchEventDetails}
                  onDelete={() => handleRemoveRSVP(rsvp.rsvpStudentId, event.id)}
                >
                  <div className="border p-4 rounded-md cursor-pointer hover:bg-gray-100 transition-colors">
                    <div className="flex justify-between items-center">
                      <span className="font-medium">Student ID: {rsvp.rsvpStudentId}</span>
                      {user?.id === rsvp.rsvpStudentId && (
                        <Button variant="ghost" size="sm">
                          Edit
                        </Button>
                      )}
                    </div>
                    {rsvp.tickets.length > 0 && (
                      <ul className="mt-2 space-y-1">
                        {rsvp.tickets.map((ticket: any) => (
                          <li key={ticket.ticketStudentId} className="ml-4">
                            - Guest: {ticket.ticketStudentId}
                            {ticket.specialPreferences && ` (${ticket.specialPreferences})`}
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                </RSVPDialog>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

export default EventDetails;
