import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { NavLink } from 'react-router-dom';
import { ArrowUpRight } from 'lucide-react';
import EventsTable from '@/components/EventsTable';
import ClubsSmallList from '@/components/ClubsSmallList';
import SmallEventCard from '@/components/SmallEventCard';
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from '@/components/ui/carousel';
import axiosInstance from '@/api/apiConfig';
import { Event } from '@/types/Event';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuth } from '@/hooks/AuthProvider';

interface Ticket {
  specialPreferences: string;
  eventName: string;
  rsvpStudentId: number;
  eventId: number;
  ticketStudentId: number;
}

const Home: React.FC = () => {
  const [upcomingEvents, setUpcomingEvents] = useState<Ticket[]>([]);
  const [allEvents, setAllEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState({ tickets: true, events: true });
  const [error, setError] = useState<string | null>(null);
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    const fetchUpcomingEvents = async () => {
      if (!user) return;
      try {
        const response = await axiosInstance.get<Ticket[]>(
          `/rsvps/ticketsForStudent?studentId=${user.id}`
        );
        setUpcomingEvents(response.data);
      } catch (err) {
        console.error('Failed to fetch upcoming events:', err);
        setError('Failed to load upcoming events.');
      } finally {
        setLoading((prev) => ({ ...prev, tickets: false }));
      }
    };

    const fetchAllEvents = async () => {
      try {
        const response = await axiosInstance.get<Event[]>('/events');
        setAllEvents(response.data.slice(0, 10)); // Limit to 10 events
      } catch (err) {
        console.error('Failed to fetch all events:', err);
        setError('Failed to load events.');
      } finally {
        setLoading((prev) => ({ ...prev, events: false }));
      }
    };

    if (isAuthenticated) {
      fetchUpcomingEvents();
    } else {
      setLoading((prev) => ({ ...prev, tickets: false }));
    }
    fetchAllEvents();
  }, [user, isAuthenticated]);

  const renderUpcomingEvents = () => {
    if (!isAuthenticated) {
      return (
        <div className="flex items-center justify-center h-48">
          <p className="text-lg text-gray-500">You need to login to see your upcoming events</p>
        </div>
      );
    }

    if (loading.tickets) {
      return <Skeleton className="h-48 w-full mt-4" />;
    }
    if (error) {
      return <div>{error}</div>;
    }

    if (upcomingEvents.length === 0) {
      return (
        <div className="flex items-center justify-center h-48">
          <p className="text-lg text-gray-500">You have no upcoming events</p>
        </div>
      );
    }

    return (
      <Carousel opts={{ align: 'start', loop: true }} className="w-full py-4">
        <CarouselContent>
          {upcomingEvents.map((event, index) => (
            <CarouselItem key={index} className="md:basis-1/2 lg:basis-1/3">
              <SmallEventCard
                eventId={event.eventId}
                eventName={event.eventName}
                specialPreferences={event.specialPreferences}
                className="basis-1/3"
              />
            </CarouselItem>
          ))}
        </CarouselContent>
        <CarouselPrevious />
        <CarouselNext />
      </Carousel>
    );
  };

  return (
    <div className="flex flex-1 flex-col gap-4 p-4 md:gap-8 md:p-8">
      <div>
        <h2 className="text-2xl font-semibold leading-none tracking-tight">Your upcoming events</h2>
        {renderUpcomingEvents()}
      </div>

      <div className="grid gap-4 md:gap-8 lg:grid-cols-2 xl:grid-cols-3">
        <Card className="xl:col-span-2">
          <CardHeader className="flex flex-row items-center">
            <div className="grid gap-2">
              <CardTitle>Events</CardTitle>
              <CardDescription>Upcoming Events (showing up to 10)</CardDescription>
            </div>
            <Button asChild size="sm" className="ml-auto gap-1">
              <NavLink to="/events">
                View All
                <ArrowUpRight className="h-4 w-4" />
              </NavLink>
            </Button>
          </CardHeader>
          <CardContent>
            {loading.events ? (
              <Skeleton className="h-48 w-full" />
            ) : (
              <EventsTable events={allEvents} />
            )}
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center">
            <CardTitle>Recommended Clubs</CardTitle>
            <Button asChild size="sm" className="ml-auto gap-1">
              <NavLink to="/clubs">
                View All
                <ArrowUpRight className="h-4 w-4" />
              </NavLink>
            </Button>
          </CardHeader>
          <CardContent className="grid gap-4 p-0 px-4">
            <ClubsSmallList />
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Home;
