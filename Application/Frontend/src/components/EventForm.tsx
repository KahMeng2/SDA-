import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
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
import { format, parseISO, set } from 'date-fns';
import axiosInstance from '@/api/apiConfig';
import { Event } from '@/types/Event';
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover';
import { Calendar } from './ui/calendar';
import { CalendarIcon } from 'lucide-react';

const formSchema = z.object({
  name: z.string().min(1, 'Event name is required'),
  description: z.string().min(1, 'Description is required'),
  startDate: z.date({
    required_error: 'Start date is required',
  }),
  startTime: z.string().min(1, 'Start time is required'),
  endDate: z.date({
    required_error: 'End date is required',
  }),
  endTime: z.string().min(1, 'End time is required'),
  cost: z.string().min(1, 'Cost is required'),
  capacity: z.string().min(1, 'Capacity is required'),
  isOnline: z.boolean(),
  venueId: z.string().min(1, 'Venue is required'),
});

interface Venue {
  id: number;
  description: string;
}

interface EventFormProps {
  clubId: string;
  event?: Event;
  onSubmit: (data: Record<string, string>) => void;
  onCancel: () => void;
}

const EventForm: React.FC<EventFormProps> = ({ clubId, event, onSubmit }) => {
  const [venues, setVenues] = useState<Venue[]>([]);
  const [isOnline, setIsOnline] = useState(event ? event.isOnline : false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: event?.name || '',
      description: event?.description || '',
      startDate: event ? parseISO(event.startTime) : undefined,
      startTime: event ? format(parseISO(event.startTime), 'HH:mm') : '',
      endDate: event ? parseISO(event.endTime) : undefined,
      endTime: event ? format(parseISO(event.endTime), 'HH:mm') : '',
      cost: event?.cost.toString() || '',
      capacity: event?.capacity.toString() || '',
      isOnline: event ? event.isOnline : false,
      venueId: event?.venueId.toString() || '',
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
      }
    };

    fetchVenues();
  }, [isOnline]);

  const handleSubmit = (values: z.infer<typeof formSchema>) => {
    const formatDateTimeToUTC = (date: Date, timeString: string) => {
      const [hours, minutes] = timeString.split(':');
      const dateTime = set(date, { hours: parseInt(hours), minutes: parseInt(minutes) });
      return format(dateTime, "yyyy-MM-dd'T'HH:mm:ss'+00:00'");
    };
    const formattedData: Record<string, string> = {
      ...Object.entries(values).reduce(
        (acc, [key, value]) => {
          if (value instanceof Date) {
            acc[key] = format(value, 'yyyy-MM-dd');
          } else {
            acc[key] = value.toString();
          }
          return acc;
        },
        {} as Record<string, string>
      ),
      startTime: formatDateTimeToUTC(values.startDate, values.startTime),
      endTime: formatDateTimeToUTC(values.endDate, values.endTime),
      clubId: clubId,
      numTickets: (event?.numTickets || 0).toString(),
      isCancelled: (event?.isCancelled || false).toString(),
    };

    onSubmit(formattedData);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="gap-4 grid grid-cols-2">
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
          name="startDate"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel>Start Date</FormLabel>
              <Popover>
                <PopoverTrigger asChild>
                  <FormControl>
                    <Button
                      variant={'outline'}
                      className={`w-full pl-3 text-left font-normal ${!field.value && 'text-muted-foreground'}`}
                    >
                      {field.value ? format(field.value, 'PPP') : <span>Pick a date</span>}
                      <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                    </Button>
                  </FormControl>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={field.value}
                    onSelect={field.onChange}
                    disabled={(date) => date < new Date()}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
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
        <FormField
          control={form.control}
          name="endDate"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel>End Date</FormLabel>
              <Popover>
                <PopoverTrigger asChild>
                  <FormControl>
                    <Button
                      variant={'outline'}
                      className={`w-full pl-3 text-left font-normal ${!field.value && 'text-muted-foreground'}`}
                    >
                      {field.value ? format(field.value, 'PPP') : <span>Pick a date</span>}
                      <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                    </Button>
                  </FormControl>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={field.value}
                    onSelect={field.onChange}
                    disabled={(date) => date < new Date()}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
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
        <FormField
          control={form.control}
          name="cost"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Cost</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  step="0.01"
                  placeholder="Enter cost"
                  {...field}
                  onChange={(e) => field.onChange(e.target.value)}
                />
              </FormControl>
              <FormMessage />
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
        <FormField
          control={form.control}
          name="capacity"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Capacity</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  placeholder="Enter capacity"
                  {...field}
                  onChange={(e) => field.onChange(e.target.value)}
                />
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

        <Button type="submit" className="col-span-2">
          {event ? 'Update Event' : 'Create Event'}
        </Button>
      </form>
    </Form>
  );
};

export default EventForm;
