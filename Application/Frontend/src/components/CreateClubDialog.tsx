import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { toast } from '@/hooks/use-toast';
import axiosInstance from '@/api/apiConfig';

const formSchema = z.object({
  name: z.string().min(1, 'Club name is required'),
  adminId: z.string().min(1, 'Admin ID is required'),
  balance: z
    .string()
    .regex(/^\d+(\.\d{1,2})?$/, 'Balance must be a valid number with up to 2 decimal places'),
});

type FormData = z.infer<typeof formSchema>;

interface CreateClubDialogProps {
  children: React.ReactNode;
  onClubCreated: () => void;
}

const CreateClubDialog: React.FC<CreateClubDialogProps> = ({ children, onClubCreated }) => {
  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: '',
      adminId: '',
      balance: '0.00',
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      await axiosInstance.post('/clubs', data);
      toast({
        title: 'Club Created',
        description: `The club "${data.name}" has been successfully created.`,
      });
      form.reset();
      onClubCreated();
    } catch (error) {
      toast({
        title: 'Error',
        description: 'There was an error creating the club. Please try again.',
        variant: 'destructive',
      });
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create New Club</DialogTitle>
          <DialogDescription>
            Enter the details for the new club. Click save when you're done.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Club Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Enter club name" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="adminId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Admin ID</FormLabel>
                  <FormControl>
                    <Input placeholder="Enter admin ID" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="balance"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Initial Balance</FormLabel>
                  <FormControl>
                    <Input type="number" step="0.01" placeholder="0.00" {...field} />
                  </FormControl>
                  <FormDescription>
                    Enter the initial balance (up to 2 decimal places)
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DialogFooter>
              <Button type="submit">Create Club</Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateClubDialog;
