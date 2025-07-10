import React, { useState } from 'react';
import axiosInstance from '@/api/apiConfig';
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

interface DeleteClubDialogProps {
  clubId: string | number; // Accept either string or number for flexibility
  onDeleteSuccess: () => void;
  children: React.ReactNode;
}

const DeleteClubDialog: React.FC<DeleteClubDialogProps> = ({
  clubId,
  onDeleteSuccess,
  children,
}) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async () => {
    setIsDeleting(true);
    setError(null);
    try {
      // Ensure clubId is sent as a string
      const clubIdString = clubId.toString();
      await axiosInstance.delete('/clubs', {
        data: { clubId: clubIdString },
      });
      toast({
        title: 'Club Deleted',
        description: `The club "${clubId}" has been successfully deleted.`,
        variant: 'destructive',
      });
      onDeleteSuccess();
    } catch (e) {
      toast({
        title: 'Error',
        description: `Failed to delete club`,
        variant: 'destructive',
      });
      setError('Failed to delete the club. Please try again.');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>{children}</AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure you want to delete this club?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone. This will permanently delete the club and all associated
            data.
          </AlertDialogDescription>
        </AlertDialogHeader>
        {error && <p className="text-red-500 text-sm">{error}</p>}
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction onClick={handleDelete} disabled={isDeleting}>
            {isDeleting ? 'Deleting...' : 'Delete'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default DeleteClubDialog;
