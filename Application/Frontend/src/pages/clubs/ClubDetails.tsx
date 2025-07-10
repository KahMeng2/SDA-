import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/apiConfig';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { ChevronLeft, CogIcon, UserPlus, Trash, PlusCircle } from 'lucide-react';
import EventsTable from '@/components/EventsTable';
import AdminsList from '@/components/AdminsList';
import AddAdminDialog from '@/components/AddAdminDialog';
import DeleteClubDialog from '@/components/DeleteClubDialog';
import ErrorDisplay from '@/components/ErrorDisplay';
import { useToast } from '@/hooks/use-toast';
import { Event } from '@/types/Event';
import { User } from '@/types/User';
import { Club } from '@/types/Clubs';
import EventForm from '@/components/EventForm';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import axios from 'axios';
import FundingApplicationList from '../../components/FundingApplicationList';
import FundingApplicationModal from '../../components/FundingApplicationModal';
import { FundingApplication } from '@/types/FundingApplication';

const ClubDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [club, setClub] = useState<Club | null>(null);
  const [events, setEvents] = useState<Event[]>([]);
  const [admins, setAdmins] = useState<User[]>([]);
  const [showFundingModal, setShowFundingModal] = useState(false);
  const [loading, setLoading] = useState({
    club: true,
    events: true,
    admins: true,
    fundingApplications: true,
  });
  const [error, setError] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [isEditEventDialogOpen, setIsEditEventDialogOpen] = useState(false);
  const [fundingApplications, setFundingApplications] = useState<FundingApplication[]>([]);
  const [selectedApplication, setSelectedApplication] = useState<FundingApplication | null>(null);

  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);

  useEffect(() => {
    const userDataString = localStorage.getItem('user');
    if (userDataString) {
      const userData: User = JSON.parse(userDataString);
      setUser(userData);
    }
  }, []);

  const fetchClubDetails = useCallback(async () => {
    try {
      const response = await axiosInstance.get<Club>(`/clubs/${id}`);
      setClub(response.data);
    } catch (e) {
      setError('Failed to fetch club details');
    } finally {
      setLoading((prev) => ({ ...prev, club: false }));
    }
  }, [id]);

  const fetchClubEvents = useCallback(async () => {
    try {
      const response = await axiosInstance.get<Event[]>(`/events/forClub?clubId=${id}`);
      setEvents(response.data);
    } catch (e) {
      console.error('Failed to fetch club events', e);
    } finally {
      setLoading((prev) => ({ ...prev, events: false }));
    }
  }, [id]);

  const fetchClubAdmins = useCallback(async () => {
    try {
      const response = await axiosInstance.get<User[]>(`/students/adminsForClub?clubId=${id}`);
      setAdmins(response.data);
    } catch (e) {
      console.error('Failed to fetch club admins', e);
    } finally {
      setLoading((prev) => ({ ...prev, admins: false }));
    }
  }, [id]);
  const fetchFundingApplications = useCallback(async () => {
    try {
      const response = await axiosInstance.get<FundingApplication[]>(
        `/funding_application/club/${id}`
      );
      setFundingApplications(response.data);
    } catch (e) {
      console.error('Failed to fetch funding applications', e);
    } finally {
      setLoading((prev) => ({ ...prev, fundingApplications: false }));
    }
  }, [id]);

  const handleDeleteSuccess = () => {
    navigate('/clubs');
    toast({
      title: 'Club Deleted',
      description: 'The club has been successfully deleted.',
    });
  };

  const handleAddAdmin = async (newAdminId: string) => {
    if (!user) {
      toast({
        title: 'Error',
        description: 'User information not available.',
        variant: 'destructive',
      });
      return;
    }

    try {
      await axiosInstance.post('/students/makeAdmin', {
        clubId: id,
        existingAdminId: user.id.toString(),
        newAdminId,
      });
      await fetchClubAdmins();
      toast({
        title: 'Admin Added',
        description: 'New admin has been successfully added to the club.',
      });
    } catch (e) {
      console.error('Failed to add admin', e);
      if (axios.isAxiosError(e) && e.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to add admins to this club.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'Error',
          description: 'Failed to add new admin. Please try again.',
          variant: 'destructive',
        });
      }
    }
  };

  const handleRemoveAdmin = async (adminId: string) => {
    if (!user) {
      toast({
        title: 'Error',
        description: 'User information not available.',
        variant: 'destructive',
      });
      return;
    }

    try {
      await axiosInstance.delete('/students/revokeAdmin', {
        data: {
          clubId: id,
          existingAdminId: user.id.toString(),
          revokedAdminId: adminId,
        },
      });
      await fetchClubAdmins();
      toast({
        title: 'Admin Removed',
        description: 'Admin has been successfully removed from the club.',
      });
    } catch (e) {
      console.error('Failed to remove admin', e);
      if (axios.isAxiosError(e) && e.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to remove admins from this club.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'Error',
          description: 'Failed to remove admin. Please try again.',
          variant: 'destructive',
        });
      }
    }
  };
  const [isLocked, setIsLocked] = useState(false);

  const handleEditEvent = useCallback(
    async (event: Event) => {
      try {
        await axiosInstance.put(`/events/getResource?eventId=${event.id}`, {
          clubId: id,
        });
        setIsLocked(true);
        setSelectedEvent(event);
        setIsEditEventDialogOpen(true);
      } catch (error) {
        console.error('Failed to acquire lock:', error);
        if (axios.isAxiosError(error) && error.response?.status === 409) {
          toast({
            title: 'Event Locked',
            description: 'This event is currently being edited by another user.',
            variant: 'destructive',
          });
        } else {
          toast({
            title: 'Error',
            description: 'Failed to edit the event. Please try again.',
            variant: 'destructive',
          });
        }
      }
    },
    [id]
  );

  const handleCloseEventDialog = useCallback(async () => {
    if (isLocked && selectedEvent) {
      try {
        await axiosInstance.put(`/events/releaseResource?eventId=${selectedEvent.id}`, {
          clubId: id,
        });
      } catch (error) {
        console.error('Failed to release lock:', error);
      }
    }
    setIsLocked(false);
    setIsEditEventDialogOpen(false);
    setSelectedEvent(null);
  }, [isLocked, selectedEvent, id]);

  const handleEventSubmit = async (data: Record<string, string>) => {
    try {
      if (selectedEvent) {
        await axiosInstance.put(`/events/executeUpdate?eventId=${selectedEvent.id}`, data);
        toast({
          title: 'Event Updated',
          description: 'The event has been successfully updated.',
        });
      } else {
        await axiosInstance.post('/events', data);
        toast({
          title: 'Event Created',
          description: 'The new event has been successfully created.',
        });
      }
      setIsEditEventDialogOpen(false);
      setSelectedEvent(null);
      setIsLocked(false);
      fetchClubEvents();
    } catch (error) {
      console.error('Failed to submit event:', error);
      if (axios.isAxiosError(error) && error.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to perform this action.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'Error',
          description: 'Failed to submit the event. Please try again.',
          variant: 'destructive',
        });
      }
    }
  };
  const handleCreateEvent = useCallback(() => {
    console.log('Create event clicked');
    setSelectedEvent(null);
    setIsEditEventDialogOpen(true);
  }, []);

  // const handleEditEvent = useCallback((event: Event) => {
  //   console.log('Edit event clicked:', event);
  //   setSelectedEvent(event);
  //   setIsEditEventDialogOpen(true);
  // }, []);

  const handleCancelEvent = useCallback(
    async (eventId: number) => {
      console.log('Cancel event clicked:', eventId);
      try {
        await axiosInstance.put(`/events/cancel?eventId=${eventId}`);
        toast({
          title: 'Event Cancelled',
          description: 'The event has been successfully cancelled.',
        });
        fetchClubEvents();
      } catch (error) {
        console.error('Failed to cancel event:', error);
        if (axios.isAxiosError(error) && error.response?.status === 403) {
          toast({
            title: 'Permission Denied',
            description: 'You are not authorized to cancel this event.',
            variant: 'destructive',
          });
        } else {
          toast({
            title: 'Error',
            description: 'Failed to cancel the event. Please try again.',
            variant: 'destructive',
          });
        }
      }
    },
    [fetchClubEvents, toast]
  );

  // const handleEventSubmit = async (data: Record<string, string>) => {
  //   try {
  //     if (selectedEvent) {
  //       await axiosInstance.put(`/events?eventId=${selectedEvent.id}`, data);
  //       toast({
  //         title: 'Event Updated',
  //         description: 'The event has been successfully updated.',
  //       });
  //     } else {
  //       await axiosInstance.post('/events', data);
  //       toast({
  //         title: 'Event Created',
  //         description: 'The new event has been successfully created.',
  //       });
  //     }
  //     setIsEditEventDialogOpen(false);
  //     setSelectedEvent(null);
  //     fetchClubEvents();
  //   } catch (error) {
  //     console.error('Failed to submit event:', error);
  //     if (axios.isAxiosError(error) && error.response?.status === 403) {
  //       toast({
  //         title: 'Permission Denied',
  //         description: 'You are not authorized to perform this action.',
  //         variant: 'destructive',
  //       });
  //     } else {
  //       toast({
  //         title: 'Error',
  //         description: 'Failed to submit the event. Please try again.',
  //         variant: 'destructive',
  //       });
  //     }
  //   }
  // };
  useEffect(() => {
    fetchClubDetails();
    fetchClubEvents();
    fetchClubAdmins();
    fetchFundingApplications();
  }, [fetchClubDetails, fetchClubEvents, fetchClubAdmins, fetchFundingApplications]);

  const isUserAdmin = user && user.administratedClubs.some((club) => club.id.toString() === id);

  if (error) return <ErrorDisplay message={error} />;

  return (
    <div className="flex flex-col w-full gap-2">
      <header className="flex items-center justify-between py-4">
        <div className="flex items-center gap-4">
          <Button
            variant="outline"
            size="icon"
            className="h-9 w-9"
            onClick={() => navigate('/clubs')}
          >
            <ChevronLeft className="h-4 w-4" />
            <span className="sr-only">Back</span>
          </Button>

          {loading.club ? (
            <Skeleton className="h-9 w-48" />
          ) : (
            <h1 className="text-3xl font-semibold tracking-tight">{club?.name}</h1>
          )}
        </div>

        {isUserAdmin && (
          <div className="flex gap-2">
            <Button variant="outline" size="sm">
              <CogIcon className="h-4 w-4 mr-2" />
              Edit
            </Button>
            <DeleteClubDialog clubId={id!} onDeleteSuccess={handleDeleteSuccess}>
              <Button variant="destructive" size="sm">
                <Trash className="h-4 w-4 mr-2" />
                Delete
              </Button>
            </DeleteClubDialog>
          </div>
        )}
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="md:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Upcoming Events</CardTitle>
            <Button onClick={handleCreateEvent}>
              <PlusCircle className="mr-2 h-4 w-4" />
              Create Event
            </Button>
          </CardHeader>
          <CardContent>
            {loading.events ? (
              <div className="space-y-2">
                <Skeleton className="h-12 w-full" />
                <Skeleton className="h-12 w-full" />
                <Skeleton className="h-12 w-full" />
              </div>
            ) : (
              <EventsTable
                events={events}
                showActions={true}
                onEditEvent={handleEditEvent}
                onCancelEvent={handleCancelEvent}
              />
            )}
          </CardContent>
        </Card>

        <div className="flex flex-col gap-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Admins</CardTitle>
              <AddAdminDialog onAddAdmin={handleAddAdmin}>
                <Button variant="outline" size="icon">
                  <UserPlus className="h-4 w-4" />
                </Button>
              </AddAdminDialog>
            </CardHeader>
            <CardContent>
              {loading.admins ? (
                <div className="space-y-2">
                  <Skeleton className="h-6 w-full" />
                  <Skeleton className="h-6 w-full" />
                  <Skeleton className="h-6 w-full" />
                </div>
              ) : (
                <AdminsList admins={admins} onRemoveAdmin={handleRemoveAdmin} />
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <div>
                <CardTitle>Funding</CardTitle>
                {club && (
                  <p className="text-sm text-muted-foreground">
                    Balance: ${club.balance.toFixed(2)}
                  </p>
                )}
              </div>
              <Button
                onClick={() => {
                  setSelectedApplication(null);
                  setShowFundingModal(true);
                }}
              >
                <PlusCircle className="mr-2 h-4 w-4" />
                New Application
              </Button>
            </CardHeader>
            <CardContent>
              {loading.fundingApplications ? (
                <Skeleton className="h-48 w-full" />
              ) : (
                <FundingApplicationList
                  applications={fundingApplications}
                  onApplicationClick={(app) => {
                    setSelectedApplication(app);
                    setShowFundingModal(true);
                  }}
                />
              )}
            </CardContent>
          </Card>
        </div>
      </div>
      <Dialog open={isEditEventDialogOpen} onOpenChange={handleCloseEventDialog}>
        <DialogContent className="sm:max-w-[1000px]">
          <DialogHeader>
            <DialogTitle>{selectedEvent ? 'Edit Event' : 'Create New Event'}</DialogTitle>
            <DialogDescription>
              {selectedEvent
                ? 'Make changes to your event here.'
                : 'Add the details for your new event.'}
            </DialogDescription>
          </DialogHeader>
          <EventForm
            clubId={id!}
            event={selectedEvent || undefined}
            onSubmit={handleEventSubmit}
            onCancel={handleCloseEventDialog}
          />
        </DialogContent>
      </Dialog>

      {/* <Dialog open={isEditEventDialogOpen} onOpenChange={setIsEditEventDialogOpen}>
        <DialogContent className="sm:max-w-[1000px]">
          <DialogHeader>
            <DialogTitle>{selectedEvent ? 'Edit Event' : 'Create New Event'}</DialogTitle>
            <DialogDescription>
              {selectedEvent
                ? 'Make changes to your event here.'
                : 'Add the details for your new event.'}
            </DialogDescription>
          </DialogHeader>
          <EventForm clubId={id!} event={selectedEvent || undefined} onSubmit={handleEventSubmit} />
        </DialogContent>
      </Dialog> */}

      <FundingApplicationModal
        application={selectedApplication}
        clubId={parseInt(id!)}
        open={showFundingModal}
        onClose={() => {
          setShowFundingModal(false);
          setSelectedApplication(null);
        }}
        onRefresh={fetchFundingApplications}
      />
    </div>
  );
};

export default ClubDetails;
