export interface Event {
  id: number;
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  cost: number;
  numTickets: number;
  capacity: number;
  clubId: number;
  clubName?: string;
  venueId: number;
  isCancelled: boolean;
  isOnline: boolean;
}

export { Event };
