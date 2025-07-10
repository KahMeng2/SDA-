import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Separator } from '@/components/ui/separator';
import { Button } from '@/components/ui/button';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, useFieldArray } from 'react-hook-form';
import { z } from 'zod';
import { toast } from '@/hooks/use-toast';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { PlusCircle, X, Trash } from 'lucide-react';
import axiosInstance from '@/api/apiConfig';
import { useAuth } from '@/hooks/AuthProvider';
import axios from 'axios';

interface RSVPDialogProps {
  children: React.ReactNode;
  eventName: string;
  eventId: string;
  onRSVPSuccess: () => void;
  existingRSVP?: {
    rsvpStudentId: number;
    tickets: {
      ticketStudentId: number;
      specialPreferences: string;
    }[];
  };
  onDelete: (rsvpStudentId: number, eventId: number) => Promise<void>;
}

const TicketSchema = z.object({
  ticketStudentId: z.string().min(1, 'Student ID is required'),
  specialPreferences: z.string().optional(),
});

const FormSchema = z.object({
  tickets: z.array(TicketSchema).min(1, 'At least one ticket is required'),
});

const RSVPDialog: React.FC<RSVPDialogProps> = ({
  children,
  eventName,
  eventId,
  onRSVPSuccess,
  existingRSVP,
  onDelete,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const { user } = useAuth();

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      tickets: existingRSVP
        ? existingRSVP.tickets.map((ticket) => ({
            ticketStudentId: ticket.ticketStudentId.toString(),
            specialPreferences: ticket.specialPreferences || '',
          }))
        : [{ ticketStudentId: user?.id.toString() || '', specialPreferences: '' }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'tickets',
  });

  useEffect(() => {
    if (existingRSVP) {
      form.reset({
        tickets: existingRSVP.tickets.map((ticket) => ({
          ticketStudentId: ticket.ticketStudentId.toString(),
          specialPreferences: ticket.specialPreferences || '',
        })),
      });
    }
  }, [existingRSVP, form]);

  async function onSubmit(data: z.infer<typeof FormSchema>) {
    try {
      const formattedData = {
        rsvpStudentId: (existingRSVP?.rsvpStudentId || user?.id || '').toString(),
        eventId: eventId,
        tickets: data.tickets.map((ticket) => ({
          ticketStudentId: ticket.ticketStudentId,
          specialPreferences: ticket.specialPreferences || '',
        })),
      };

      if (existingRSVP) {
        await axiosInstance.put('/rsvps', formattedData);
      } else {
        await axiosInstance.post('/rsvps', formattedData);
      }

      toast({
        title: existingRSVP ? 'RSVP Updated' : 'RSVP Successful',
        description: existingRSVP
          ? 'Your RSVP has been successfully updated.'
          : `You have successfully RSVP'd for the event.`,
      });

      setIsOpen(false);
      onRSVPSuccess();
    } catch (error) {
      console.error('RSVP submission error:', error);
      if (axios.isAxiosError(error) && error.response?.status === 403) {
        toast({
          title: 'Permission Denied',
          description: 'You are not authorized to RSVP for this event.',
          variant: 'destructive',
        });
      } else {
        toast({
          title: 'RSVP Failed',
          description: 'There was an error submitting your RSVP. Please try again.',
          variant: 'destructive',
        });
      }
    }
  }

  const handleDelete = async () => {
    if (existingRSVP) {
      try {
        await onDelete(existingRSVP.rsvpStudentId, parseInt(eventId));
        setIsOpen(false);
      } catch (error) {
        // Error handling is done in the parent component
        console.error('Error deleting RSVP:', error);
      }
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[750px]">
        <DialogHeader>
          <DialogTitle>{existingRSVP ? 'Edit RSVP' : 'RSVP for event'}</DialogTitle>
          <DialogDescription>{eventName}</DialogDescription>
        </DialogHeader>
        <Separator />
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            {fields.map((field, index) => (
              <div key={field.id} className="space-y-4">
                <FormField
                  control={form.control}
                  name={`tickets.${index}.ticketStudentId`}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Student ID</FormLabel>
                      <FormControl>
                        <Input {...field} disabled={index === 0 && existingRSVP !== undefined} />
                      </FormControl>
                      {index === 0 && <FormDescription>This is your student ID.</FormDescription>}
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name={`tickets.${index}.specialPreferences`}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Special Preferences</FormLabel>
                      <FormControl>
                        <Input {...field} placeholder="e.g., Vegan, Wheelchair access" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                {index > 0 && (
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    className="mt-2"
                    onClick={() => remove(index)}
                  >
                    <X className="h-4 w-4 mr-2" />
                    Remove Ticket
                  </Button>
                )}
              </div>
            ))}
            <Button
              type="button"
              variant="outline"
              size="sm"
              className="mt-2"
              onClick={() => append({ ticketStudentId: '', specialPreferences: '' })}
            >
              <PlusCircle className="h-4 w-4 mr-2" />
              Add Another Ticket
            </Button>
            <div className="flex justify-between">
              <Button type="submit" className="flex-grow mr-2">
                {existingRSVP ? 'Update RSVP' : 'Submit RSVP'}
              </Button>
              {existingRSVP && (
                <Button type="button" variant="destructive" onClick={handleDelete}>
                  <Trash className="h-4 w-4 mr-2" />
                  Delete RSVP
                </Button>
              )}
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export default RSVPDialog;
