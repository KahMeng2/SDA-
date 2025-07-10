import { format } from 'date-fns';

const formatDate = (dateString: string) => {
  return format(new Date(dateString), 'EEE, MMM d, yyyy, h:mm a');
};

export default formatDate;
