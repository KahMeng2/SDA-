import React from 'react';
import { Button } from '@/components/ui/button';
import { Trash } from 'lucide-react';
import { User } from '@/types/User';

interface AdminsListProps {
  admins: User[];
  onRemoveAdmin: (adminId: string) => void;
}

const AdminsList: React.FC<AdminsListProps> = ({ admins, onRemoveAdmin }) => {
  return (
    <ul className="space-y-2">
      {admins.map((admin) => (
        <li key={admin.id} className="flex justify-between items-center">
          <span>{`${admin.firstName} ${admin.lastName}`}</span>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onRemoveAdmin(admin.id.toString())}
            className="text-red-500 hover:text-red-700"
          >
            <Trash className="h-4 w-4" />
          </Button>
        </li>
      ))}
    </ul>
  );
};

export default AdminsList;
