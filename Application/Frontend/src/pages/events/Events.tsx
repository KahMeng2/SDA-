import React, { useState, useEffect } from 'react';
import { Separator } from '@/components/ui/separator';
import axiosInstance from '@/api/apiConfig';
import { Event } from '@/types/Event';
import { Skeleton } from '@/components/ui/skeleton';
import { AlertCircle } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Card, CardContent } from '@/components/ui/card';
import EventsTable from '@/components/EventsTable';
import { Input } from '@/components/ui/input';
import { Toggle } from '@/components/ui/toggle';
import { Button } from '@/components/ui/button';
import debounce from 'lodash/debounce';

function Events() {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [nameQuery, setNameQuery] = useState('');
  const [isUpcoming, setIsUpcoming] = useState<boolean | null>(null);
  const [isOnline, setIsOnline] = useState<boolean | null>(null);
  const [isInPerson, setIsInPerson] = useState<boolean | null>(null);
  const [isCancelled, setIsCancelled] = useState<boolean | null>(null);

  const fetchEvents = async (filters: {
    nameQuery?: string;
    isUpcoming?: boolean | null;
    isOnline?: boolean | null;
    isInPerson?: boolean | null;
    isCancelled?: boolean | null;
  }) => {
    try {
      setLoading(true);
      const params = new URLSearchParams();

      if (filters.nameQuery) params.append('nameQuery', filters.nameQuery);
      if (typeof filters.isUpcoming === 'boolean')
        params.append('isUpcoming', filters.isUpcoming.toString());
      if (typeof filters.isOnline === 'boolean')
        params.append('isOnline', filters.isOnline.toString());
      if (typeof filters.isInPerson === 'boolean')
        params.append('isInPerson', filters.isInPerson.toString());
      if (typeof filters.isCancelled === 'boolean')
        params.append('isCancelled', filters.isCancelled.toString());

      const response = await axiosInstance.get<Event[]>(`/events/filter?${params.toString()}`);
      setEvents(response.data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch events:', err);
      setError('Failed to load events. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const debouncedFetchEvents = debounce(fetchEvents, 300);

  useEffect(() => {
    fetchEvents({});
  }, []);

  useEffect(() => {
    const filters: {
      nameQuery?: string;
      isUpcoming?: boolean | null;
      isOnline?: boolean | null;
      isInPerson?: boolean | null;
      isCancelled?: boolean | null;
    } = {};
    if (nameQuery) filters.nameQuery = nameQuery;
    if (isUpcoming !== null) filters.isUpcoming = isUpcoming;
    if (isOnline !== null) filters.isOnline = isOnline;
    if (isInPerson !== null) filters.isInPerson = isInPerson;
    if (isCancelled !== null) filters.isCancelled = isCancelled;

    debouncedFetchEvents(filters);
  }, [nameQuery, isUpcoming, isOnline, isInPerson, isCancelled]);

  const handleNameQueryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNameQuery(e.target.value);
  };

  const handleToggle = (setter: React.Dispatch<React.SetStateAction<boolean | null>>) => {
    setter((prev) => (prev === null ? true : prev === true ? false : null));
  };

  const clearFilters = () => {
    setNameQuery('');
    setIsUpcoming(null);
    setIsOnline(null);
    setIsInPerson(null);
    setIsCancelled(null);
  };

  return (
    <div className="container mx-auto py-6">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-3xl font-semibold tracking-tight">Events</h1>
      </div>
      <Separator className="my-4" />

      <div className="flex flex-wrap gap-4 mb-4">
        <Input
          placeholder="Search events..."
          value={nameQuery}
          onChange={handleNameQueryChange}
          className="max-w-sm transition-all duration-300 ease-in-out"
        />
        <Toggle
          pressed={isUpcoming === true}
          onPressedChange={() => handleToggle(setIsUpcoming)}
          className="transition-all duration-300 ease-in-out"
        >
          Upcoming
        </Toggle>
        <Toggle
          pressed={isOnline === true}
          onPressedChange={() => handleToggle(setIsOnline)}
          className="transition-all duration-300 ease-in-out"
        >
          Online
        </Toggle>
        <Toggle
          pressed={isInPerson === true}
          onPressedChange={() => handleToggle(setIsInPerson)}
          className="transition-all duration-300 ease-in-out"
        >
          In-person
        </Toggle>
        <Toggle
          pressed={isCancelled === true}
          onPressedChange={() => handleToggle(setIsCancelled)}
          className="transition-all duration-300 ease-in-out"
        >
          Cancelled
        </Toggle>
        <Button
          variant="outline"
          onClick={clearFilters}
          className="transition-all duration-300 ease-in-out"
        >
          Clear Filters
        </Button>
      </div>

      <Card className="pt-4 transition-all duration-300 ease-in-out">
        <CardContent>
          <div className="min-h-[300px] relative">
            {loading && (
              <div className="absolute inset-0 bg-background/50 backdrop-blur-sm z-10 flex items-center justify-center">
                <div className="space-y-2">
                  <p className="text-sm text-muted-foreground mb-2">Searching for events...</p>
                  <Skeleton className="h-12 w-full" />
                  <Skeleton className="h-12 w-full" />
                  <Skeleton className="h-12 w-full" />
                </div>
              </div>
            )}
            {error ? (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            ) : (
              <div
                className={`transition-opacity duration-300 ease-in-out ${loading ? 'opacity-50' : 'opacity-100'}`}
              >
                <EventsTable events={events} />
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

export default Events;
