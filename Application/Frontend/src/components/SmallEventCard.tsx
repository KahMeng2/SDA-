import { Users } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from './ui/card';
import { cn } from '@/lib/utils';
import { useNavigate } from 'react-router-dom';

interface SmallEventCardProps {
  eventId: number;
  eventName: string;
  specialPreferences?: string;
  icon?: React.ReactNode;
  className: string;
}

const SmallEventCard: React.FC<SmallEventCardProps> = ({
  eventId,
  eventName,
  specialPreferences,
  icon = <Users className="h-4 w-4 text-muted-foreground" />,
  className,
}) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/events/${eventId}`);
  };

  return (
    <Card className={cn(className, 'cursor-pointer')} onClick={handleClick}>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{eventName}</CardTitle>
        {icon}
      </CardHeader>
      <CardContent className="space-y-2">
        <div className="text-2xl font-bold">{eventName}</div>
        {specialPreferences && (
          <p className="text-xs text-muted-foreground">Preferences: {specialPreferences}</p>
        )}
      </CardContent>
    </Card>
  );
};

export default SmallEventCard;
