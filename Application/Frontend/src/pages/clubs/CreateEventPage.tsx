import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import axiosInstance from '@/api/apiConfig';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Card, CardContent } from '@/components/ui/card';
import { useToast } from '@/hooks/use-toast';
import { ChevronLeft } from 'lucide-react';
import { parseISO, format } from 'date-fns';

interface Venue {
  id: number;
  description: string;
  cost: number;
  venueCapacity: number;
  link?: string;
  address?: {
    addressLine1: string;
    addressLine2?: string;
    city: string;
    state: string;
    country: string;
    postcode: number;
  };
  floor?: string;
  room?: string;
}

const formSchema = z.object({
  name: z.string().min(1, 'Event name is required'),
  description: z.string().min(1, 'Description is required'),
  startDate: z.string().min(1, 'Start date is required'),
  startTime: z.string().min(1, 'Start time is required'),
  endDate: z.string().min(1, 'End date is required'),
  endTime: z.string().min(1, 'End time is required'),
  cost: z.string().min(1, 'Cost is required'),
  numRsvps: z.string().min(1, 'Number of RSVPs is required'),
  capacity: z.string().min(1, 'Capacity is required'),
  isOnline: z.boolean(),
  venueId: z.string().min(1, 'Venue is required'),
});

const CreateEventPage: React.FC = () => {
  const { clubId } = useParams<{ clubId: string }>();
  const navigate = useNavigate();
  const { toast } = useToast();
  const [venues, setVenues] = useState<Venue[]>([]);
  const [isOnline, setIsOnline] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: '',
      description: '',
      startDate: '',
      startTime: '',
      endDate: '',
      endTime: '',
      cost: '',
      numRsvps: '0',
      capacity: '',
      isOnline: false,
      venueId: '',
    },
  });

  useEffect(() => {
    const fetchVenues = async () => {
      try {
        const endpoint = isOnline ? '/onlinevenues' : '/physicalvenues';
        const response = await axiosInstance.get<Venue[]>(endpoint);
        setVenues(response.data);
      } catch (error) {
        console.error('Failed to fetch venues:', error);
        toast({
          title: 'Error',
          description: 'Failed to fetch venues. Please try again.',
          variant: 'destructive',
        });
      }
    };

    fetchVenues();
  }, [isOnline, toast]);

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      const formatDateTimeToUTC = (dateString: string, timeString: string) => {
        const dateTime = parseISO(`${dateString}T${timeString}`);
        return format(dateTime, "yyyy-MM-dd'T'HH:mm:ss'+00:00'");
      };
      const eventData = {
        clubId: clubId,
        isCancelled: 'false',
        isOnline: values.isOnline.toString(),
        venueId: values.venueId,
        name: values.name,
        description: values.description,
        startTime: formatDateTimeToUTC(values.startDate, values.startTime),
        endTime: formatDateTimeToUTC(values.endDate, values.endTime),
        cost: values.cost,
        numRsvps: values.numRsvps,
        capacity: values.capacity,
      };

      await axiosInstance.post('/events', eventData);
      toast({
        title: 'Success',
        description: 'Event created successfully.',
      });
      navigate(`/clubs/${clubId}`);
    } catch (error) {
      console.error('Failed to create event:', error);
      toast({
        title: 'Error',
        description: 'Failed to create event. Please try again.',
        variant: 'destructive',
      });
    }
  };

  return (
    <div className="flex flex-col w-full gap-2 pb-8">
      <header className="flex items-center justify-between py-4">
        <div className="flex items-center gap-4">
          <Button
            variant="outline"
            size="icon"
            className="h-9 w-9"
            onClick={() => navigate(`/clubs/${clubId}`)}
          >
            <ChevronLeft className="h-4 w-4" />
            <span className="sr-only">Back to Club</span>
          </Button>
          <h1 className="flex-1 shrink-0 whitespace-nowrap text-3xl font-semibold tracking-tight sm:grow-0">
            Create Event
          </h1>
        </div>
      </header>
      <Card>
        <CardContent className="py-4">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Event Name</FormLabel>
                    <FormControl>
                      <Input placeholder="Enter event name" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Description</FormLabel>
                    <FormControl>
                      <Textarea placeholder="Enter event description" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <div className="grid grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="startDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Start Date</FormLabel>
                      <FormControl>
                        <Input type="date" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="startTime"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Start Time</FormLabel>
                      <FormControl>
                        <Input type="time" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <FormField
                  control={form.control}
                  name="endDate"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>End Date</FormLabel>
                      <FormControl>
                        <Input type="date" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="endTime"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>End Time</FormLabel>
                      <FormControl>
                        <Input type="time" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              {/* <FormField
                control={form.control}
                name="startTime"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Start Time</FormLabel>
                    <FormControl>
                      <Input type="datetime-local" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="endTime"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>End Time</FormLabel>
                    <FormControl>
                      <Input type="datetime-local" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              /> */}
              <FormField
                control={form.control}
                name="cost"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Cost</FormLabel>
                    <FormControl>
                      <Input type="number" step="0.01" placeholder="Enter cost" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="capacity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Capacity</FormLabel>
                    <FormControl>
                      <Input type="number" placeholder="Enter capacity" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="isOnline"
                render={({ field }) => (
                  <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4">
                    <FormControl>
                      <Checkbox
                        checked={field.value}
                        onCheckedChange={(checked) => {
                          field.onChange(checked);
                          setIsOnline(checked as boolean);
                        }}
                      />
                    </FormControl>
                    <div className="space-y-1 leading-none">
                      <FormLabel>Online Event</FormLabel>
                      <FormDescription>Is this an online event?</FormDescription>
                    </div>
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="venueId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Venue</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a venue" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {venues.map((venue) => (
                          <SelectItem key={venue.id} value={venue.id.toString()}>
                            {venue.description}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit">Create Event</Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
};

export default CreateEventPage;
