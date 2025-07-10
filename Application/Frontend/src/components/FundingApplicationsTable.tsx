import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Badge, getFundingApplicationBadgeVariant } from '@/components/ui/badge';
import { FundingApplication } from '@/types/FundingApplication';

interface FundingApplicationsTableProps {
  applications: FundingApplication[];
}

const FundingApplicationsTable: React.FC<FundingApplicationsTableProps> = ({ applications }) => {
  const navigate = useNavigate();

  const formatDate = (date: number[]) => {
    const [year, month, day] = date;
    return new Date(year, month - 1, day).toLocaleDateString();
  };

  const getStatusBadge = (state: FundingApplication['state']) => {
    const variant = getFundingApplicationBadgeVariant(state);
    return <Badge variant={variant}>{state.replace('State', '')}</Badge>;
  };

  const handleRowClick = (id: number) => {
    navigate(`/funding-applications/${id}`);
  };

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Club ID</TableHead>
          <TableHead>Description</TableHead>
          <TableHead>Amount</TableHead>
          <TableHead>Semester</TableHead>
          <TableHead>Year</TableHead>
          <TableHead>Submitted At</TableHead>
          <TableHead>Status</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {applications.map((application) => (
          <TableRow
            key={application.id}
            onClick={() => handleRowClick(application.id)}
            className="cursor-pointer hover:bg-neutral-100"
          >
            <TableCell>{application.clubID}</TableCell>
            <TableCell>{application.description}</TableCell>
            <TableCell>${application.amount.toFixed(2)}</TableCell>
            <TableCell>{application.semester}</TableCell>
            <TableCell>{application.year}</TableCell>
            <TableCell>{formatDate(application.submittedAt)}</TableCell>
            <TableCell>{getStatusBadge(application.state)}</TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

export default FundingApplicationsTable;
