import React from 'react';
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
import { toast } from '@/hooks/use-toast';

interface DeleteConfirmationModalProps {
  children: React.ReactNode;
  eventName: string;
  onDelete: () => void;
}

const DeleteConfirmationModal: React.FC<DeleteConfirmationModalProps> = ({
  children,
  eventName,
  onDelete,
}) => {
  const handleDelete = () => {
    onDelete();
    toast({
      title: 'Event Canceled',
      description: `The event "${eventName}" has been successfully cancelled.`,
      variant: 'destructive',
    });
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>{children}</AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure you want to cancel this event?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will cancel the event "{eventName}" and notify all
            attendees.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Keep Event</AlertDialogCancel>
          <AlertDialogAction onClick={handleDelete}>Yes, cancel event</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default DeleteConfirmationModal;
