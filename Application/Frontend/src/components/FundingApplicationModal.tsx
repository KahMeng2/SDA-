import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { FundingApplication } from '@/types/FundingApplication';
import { Badge, getFundingApplicationBadgeVariant } from '@/components/ui/badge';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import axiosInstance from '@/api/apiConfig';
import { toast } from '@/hooks/use-toast';

const formSchema = z.object({
  description: z.string().min(1, 'Description is required'),
  amount: z.string().min(1, 'Amount is required'),
  semester: z.string().min(1, 'Semester is required'),
  year: z.string().length(4, 'Year must be 4 digits'),
});

interface FundingApplicationModalProps {
  application: FundingApplication | null;
  clubId: number;
  open: boolean;
  onClose: () => void;
  onRefresh: () => void;
}

const FundingApplicationModal: React.FC<FundingApplicationModalProps> = ({
  application,
  clubId,
  open,
  onClose,
  onRefresh,
}) => {
  const [isSubmitDialogOpen, setIsSubmitDialogOpen] = useState(false);
  const [isCancelDialogOpen, setIsCancelDialogOpen] = useState(false);
  const [isLocked, setIsLocked] = useState(false);
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      description: '',
      amount: '',
      semester: '1',
      year: new Date().getFullYear().toString(),
    },
  });

  useEffect(() => {
    if (
      open &&
      application &&
      (application.state === 'DraftState' || application.state === 'SubmittedState')
    ) {
      const startEdit = async () => {
        try {
          await axiosInstance.put(`/funding_application/${application.id}/startEdit`);
          setIsLocked(true);
        } catch (error) {
          console.error('Failed to start edit:', error);
          toast({
            title: 'Edit Lock Failed',
            description: 'Unable to start editing. Please try again.',
            variant: 'destructive',
          });
          onClose();
        }
      };
      startEdit();
    }
  }, [open, application]);

  useEffect(() => {
    return () => {
      if (isLocked && application) {
        const stopEdit = async () => {
          try {
            await axiosInstance.put(`/funding_application/${application.id}/stopEdit`);
          } catch (error) {
            console.error('Failed to stop edit:', error);
          }
        };
        stopEdit();
      }
    };
  }, [isLocked, application]);

  const handleSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      if (application) {
        // Update existing application
        await axiosInstance.put(`/funding_application/${application.id}/commitEdit`, {
          description: values.description,
          amount: values.amount,
        });
        toast({
          title: 'Application Updated',
          description: 'Your funding application has been successfully updated.',
        });
      } else {
        // Create new application
        await axiosInstance.post('/funding_application', {
          ...values,
          clubId: clubId.toString(),
        });
        toast({
          title: 'Application Created',
          description: 'Your funding application has been successfully created.',
        });
      }
      setIsLocked(false);
      onRefresh();
      onClose();
    } catch (error) {
      console.error('Error submitting application:', error);
      toast({
        title: 'Submission Failed',
        description: 'Failed to submit the funding application. Please try again.',
        variant: 'destructive',
      });
    }
  };

  const handleSubmitApplication = async () => {
    if (application) {
      try {
        await axiosInstance.post(`/funding_application/${application.id}/submit`);
        toast({
          title: 'Application Submitted',
          description: 'Your funding application has been successfully submitted.',
        });
        setIsLocked(false);
        onRefresh();
        onClose();
      } catch (error) {
        console.error('Error submitting application:', error);
        toast({
          title: 'Submission Failed',
          description: 'Failed to submit the funding application. Please try again.',
          variant: 'destructive',
        });
      }
    }
    setIsSubmitDialogOpen(false);
  };

  const handleCancelApplication = async () => {
    if (application) {
      try {
        await axiosInstance.post(`/funding_application/${application.id}/cancel`);
        toast({
          title: 'Application Cancelled',
          description: 'Your funding application has been successfully cancelled.',
        });
        setIsLocked(false);
        onRefresh();
        onClose();
      } catch (error) {
        console.error('Error cancelling application:', error);
        toast({
          title: 'Cancellation Failed',
          description: 'Failed to cancel the funding application. Please try again.',
          variant: 'destructive',
        });
      }
    }
    setIsCancelDialogOpen(false);
  };

  const handleCloseModal = () => {
    if (isLocked && application) {
      axiosInstance
        .put(`/funding_application/${application.id}/stopEdit`)
        .catch((error) => console.error('Failed to stop edit:', error));
    }
    setIsLocked(false);
    onClose();
  };

  useEffect(() => {
    if (application) {
      form.reset({
        description: application.description,
        amount: application.amount.toString(),
        semester: application.semester.toString(),
        year: application.year.toString(),
      });
    } else {
      form.reset({
        description: '',
        amount: '',
        semester: '1',
        year: new Date().getFullYear().toString(),
      });
    }
  }, [application, form]);
  const isEditable =
    !application || application.state === 'DraftState' || application.state === 'SubmittedState';
  const isSubmittable = application && application.state === 'DraftState';
  const isCancellable = application && application.state === 'DraftState';
  return (
    <Dialog open={open} onOpenChange={handleCloseModal}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>
            {application
              ? isEditable
                ? 'Edit Funding Application'
                : 'View Funding Application'
              : 'Create Funding Application'}
          </DialogTitle>
        </DialogHeader>
        {application && (
          <div className="mb-4">
            <span className="font-semibold mr-2">Current State:</span>
            <Badge variant={getFundingApplicationBadgeVariant(application.state)}>
              {application.state.replace('State', '')}
            </Badge>
          </div>
        )}
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="gap-4 grid grid-cols-2">
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={!isEditable} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="amount"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Amount</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={!isEditable} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="semester"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Semester</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={!isEditable} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="year"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Year</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={!isEditable} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DialogFooter className="col-span-2">
              <div className="grid grid-cols-2 col-span-2 gap-2 w-full">
                {isEditable && (
                  <Button type="submit" className="col-span-2">
                    Save Changes
                  </Button>
                )}
                {isSubmittable && (
                  <AlertDialog open={isSubmitDialogOpen} onOpenChange={setIsSubmitDialogOpen}>
                    <AlertDialogTrigger asChild>
                      <Button type="button" className="col-span-2">
                        Submit Application
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Confirm Submission</AlertDialogTitle>
                        <AlertDialogDescription>
                          Are you sure you want to submit this funding application? Once submitted,
                          it cannot be edited.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction onClick={handleSubmitApplication}>
                          Submit
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                )}
                {isCancellable && (
                  <AlertDialog open={isCancelDialogOpen} onOpenChange={setIsCancelDialogOpen}>
                    <AlertDialogTrigger asChild>
                      <Button type="button" variant="destructive" className="col-span-2">
                        Cancel Application
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Confirm Cancellation</AlertDialogTitle>
                        <AlertDialogDescription>
                          Are you sure you want to cancel this funding application? This action
                          cannot be undone.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>No, keep it</AlertDialogCancel>
                        <AlertDialogAction onClick={handleCancelApplication}>
                          Yes, cancel it
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                )}
                {!isCancellable && !isEditable && !isSubmittable && (
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={onClose}
                    className="col-span-2"
                  >
                    Close
                  </Button>
                )}
              </div>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export default FundingApplicationModal;
