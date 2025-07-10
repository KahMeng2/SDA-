import React from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { FundingApplication } from '@/types/FundingApplication';
import { Badge, getFundingApplicationBadgeVariant } from './ui/badge';

interface FundingApplicationListProps {
  applications: FundingApplication[];
  onApplicationClick: (application: FundingApplication) => void;
}

const FundingApplicationList: React.FC<FundingApplicationListProps> = ({
  applications,
  onApplicationClick,
}) => {
  const getStatusBadge = (state: FundingApplication['state']) => {
    const variant = getFundingApplicationBadgeVariant(state);
    return <Badge variant={variant}>{state.replace('State', '')}</Badge>;
  };

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Value</TableHead>
          <TableHead>Status</TableHead>
          <TableHead>Semester</TableHead>
          <TableHead>Year</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {applications.map((app) => (
          <TableRow
            key={app.id}
            onClick={() => onApplicationClick(app)}
            className="cursor-pointer hover:bg-gray-100"
          >
            <TableCell>${app.amount}</TableCell>
            <TableCell>{getStatusBadge(app.state)}</TableCell>
            <TableCell>{app.semester}</TableCell>
            <TableCell>{app.year}</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default FundingApplicationList;
