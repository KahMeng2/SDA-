package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.ClubMapper;
import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.OnlineVenueMapper;
import com.brogrammerbrigade.backend.datasource.PhysicalVenueMapper;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.OnlineVenue;
import com.brogrammerbrigade.backend.domain.PhysicalVenue;
import com.brogrammerbrigade.backend.dto.EventFilterRequest;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.LockManager;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.weld.context.http.Http;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;

public class EventService {
    private static EventService instance;

    private final EventMapper eventMapper;
    private final ClubMapper clubMapper;
    private final LockManager lockManager;

    private EventService() {
        this.eventMapper = EventMapper.getInstance();
        this.clubMapper = ClubMapper.getInstance();
        this.lockManager = new LockManager();
    }


    // Public method to provide access to the singleton instance
    public static synchronized EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }
    public List<Event> getAllEvents(){
        return eventMapper.getAllEvents();

    }

    // Public method to fetch an event by the event id
    public Event getEventById(BigInteger id){
        return eventMapper.getEvent(new Event(id));
    }

    public List<Event> getEventsForClub(BigInteger clubId) {
        if (clubId == null) {
            throw new VisibleException("clubId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        return eventMapper.getEventsForClub(new Club(clubId));
    }

    public List<Event> getEventsForVenue(BigInteger venueId, Boolean isOnline) {
        if (venueId == null) {
            throw new VisibleException("venueId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        return eventMapper.getEventsForVenue(venueId, isOnline);
    }
    public Event createEvent(EventRequest eventRequest) throws RuntimeException {
        System.out.println("Thread " + Thread.currentThread().getName() + " attempting to create event");
        // Acquires lock for the club, throws exception if fails

        try {
            lockManager.acquireLock(new Club(eventRequest.getClubId()), eventRequest.getClubId().toString(), Thread.currentThread().getName());
            System.out.println("Thread " + Thread.currentThread().getName() + " acquired lock for club " + eventRequest.getClubId());
            // Existing validation code...
            this.validateRequest(eventRequest);
            // check cost against balance
            Club eventClub = new Club(eventRequest.getClubId());
            if (eventClub.getBalance() < eventRequest.getCost()) {
                throw new VisibleException("Insufficient club balance for event", HttpServletResponse.SC_FORBIDDEN);
            }
            // Create the Event object and populate its fields from the EventRequest
            Event newEvent = new Event();
            Club newEventClub = new Club(eventRequest.getClubId());
            newEvent.setClub(newEventClub);
            newEvent.setCancelled(eventRequest.getIsCancelled());
            Boolean isOnline = eventRequest.getIsOnline();
            newEvent.setOnline(isOnline);
            if (isOnline) {
                newEvent.setOnlineVenueId(eventRequest.getVenueId());
            } else {
                newEvent.setPhysicalVenueId(eventRequest.getVenueId());
            }
            newEvent.setName(eventRequest.getName());
            newEvent.setDescription(eventRequest.getDescription());
            newEvent.setStartTime(eventRequest.getStartTime());
            newEvent.setEndTime(eventRequest.getEndTime());
            newEvent.setCost(eventRequest.getCost());
            newEvent.setNumTickets(0);

            newEvent.setCapacity(eventRequest.getCapacity());

            // check that the venue exists
            Boolean venueExists;
            if (isOnline) {
                venueExists = OnlineVenueMapper.getInstance().exists((OnlineVenue) newEvent.getVenue());
            } else {
                venueExists = PhysicalVenueMapper.getInstance().exists((PhysicalVenue) newEvent.getVenue());
            }
            if (!venueExists) {
                throw new VisibleException("Venue with id " + newEvent.getVenue().getId() + " does not exist", HttpServletResponse.SC_NOT_FOUND);
            }

            // check start time < end time
            if (!(newEvent.getStartTime().isBefore(newEvent.getEndTime()))) {
                throw new VisibleException("Start time must be before end time", HttpServletResponse.SC_BAD_REQUEST);
            }

            // check for clashes
            Event clashEvent = eventMapper.getClash(newEvent);
            if (clashEvent != null) {
                throw new VisibleException("Clash: event " + clashEvent.getId() + " is happening from " +
                        clashEvent.getStartTime() + " to " + clashEvent.getEndTime() + " at the same venue", HttpServletResponse.SC_CONFLICT);
            }

            // check that event capacity does not exceed venue capacity
            Integer venueCapacity = newEvent.getVenue().getVenueCapacity();
            if (venueCapacity < newEvent.getCapacity()) {
                throw new VisibleException("Event capacity must not exceed venue capacity", HttpServletResponse.SC_BAD_REQUEST);
            }

            // Update club balance
            newEventClub.setBalance(newEventClub.getBalance() - eventRequest.getCost());
            clubMapper.update(newEventClub);

            // Use the EventMapper to insert the event into the database
            Event createdEvent = eventMapper.insert(newEvent);
            System.out.println("Thread " + Thread.currentThread().getName() + " created event successfully");

            return createdEvent;
        } finally {
            // Release the lock
            lockManager.releaseLock(new Club(eventRequest.getClubId()), eventRequest.getClubId().toString(), Thread.currentThread().getName());
        }
    }

    public void getUpdateEventResource(BigInteger eventId, EventRequest eventRequest, String username){
        // Checks if event and club exists
        Event oldEvent = new Event(eventId);
        Club eventClub = new Club(eventRequest.getClubId());

        if (!oldEvent.exists()){
            throw new VisibleException("Event does not exist", HttpServletResponse.SC_NOT_FOUND);
        }

        if (!eventClub.exists()) {
            System.out.println(!eventClub.exists());
            System.out.println("Club does not exist");
            throw new VisibleException("Club does not exist", HttpServletResponse.SC_NOT_FOUND);
        }

        lockManager.acquireLock(new Event(eventId), eventId.toString(), username);
        lockManager.acquireLock(new Club(eventRequest.getClubId()), eventRequest.getClubId().toString(), username);
    }

    public void releaseUpdateEventResource(BigInteger eventId, EventRequest eventRequest, String username){
        if (!lockManager.releaseLock(new Event(eventId), eventId.toString(), username)){
            throw new RuntimeException("Could not release lock for event");
        };
        if (!lockManager.releaseLock(new Club(eventRequest.getClubId()), eventRequest.getClubId().toString(), username)){
            throw new RuntimeException("Could not release lock for Club");
        }
    }

    public Event updateEvent(BigInteger eventId, EventRequest eventRequest, String username) {
        try {
            // Checks if event and club exists
            Event oldEvent = new Event(eventId);
            Club eventClub = new Club(eventRequest.getClubId());

            if (!oldEvent.exists()){
                throw new VisibleException("Event does not exist", HttpServletResponse.SC_NOT_FOUND);
            }

            if (!eventClub.exists()) {
                System.out.println(!eventClub.exists());
                System.out.println("Club does not exist");
                throw new VisibleException("Club does not exist", HttpServletResponse.SC_NOT_FOUND);
            }

            // Checks if user owns lock to resource
            lockManager.isLockValid(oldEvent, eventId.toString(), username);
            lockManager.isLockValid(eventClub, eventRequest.getClubId().toString(), username);

            // Validate input parameters
            this.validateRequest(eventRequest);

            // Starts event update

            Double costIncrease = eventRequest.getCost() - oldEvent.getCost();
            if (costIncrease > eventClub.getBalance()) {
                throw new VisibleException("Insufficient club balance for event", HttpServletResponse.SC_FORBIDDEN);
            }

            // Retrieve the existing event
            Event existingEvent = eventMapper.getEvent(new Event(eventId));

            // Update the Event object with the new values
            Club newEventClub = new Club(eventRequest.getClubId());
            existingEvent.setClub(newEventClub);
            existingEvent.setCancelled(eventRequest.getIsCancelled());
            Boolean isOnline = eventRequest.getIsOnline();
            existingEvent.setOnline(eventRequest.getIsOnline());
            if (isOnline) {
                existingEvent.setOnlineVenueId(eventRequest.getVenueId());
            } else {
                existingEvent.setPhysicalVenueId(eventRequest.getVenueId());
            }
            existingEvent.setName(eventRequest.getName());
            existingEvent.setDescription(eventRequest.getDescription());
            existingEvent.setStartTime(eventRequest.getStartTime());
            existingEvent.setEndTime(eventRequest.getEndTime());
            existingEvent.setCost(eventRequest.getCost());
            existingEvent.setCapacity(eventRequest.getCapacity());

            // check that the venue exists
            Boolean venueExists;
            if (isOnline) {
                venueExists = OnlineVenueMapper.getInstance().exists((OnlineVenue) existingEvent.getVenue());
            } else {
                venueExists = PhysicalVenueMapper.getInstance().exists((PhysicalVenue) existingEvent.getVenue());
            }
            if (!venueExists) {
                throw new VisibleException("Venue with id " + existingEvent.getVenue().getId() + " does not exist", HttpServletResponse.SC_NOT_FOUND);
            }

            // check start time < end time
            if (!(existingEvent.getStartTime().isBefore(existingEvent.getEndTime()))) {
                throw new VisibleException("Start time must be before end time", HttpServletResponse.SC_BAD_REQUEST);
            }

            // check for clashes
            Event clashEvent = eventMapper.getClash(existingEvent);
            if (clashEvent != null) {
                throw new VisibleException("Clash: event " + clashEvent.getId() + " is happening from " +
                        clashEvent.getStartTime() + " to " + clashEvent.getEndTime() + " at the same venue", HttpServletResponse.SC_CONFLICT);
            }

            // check that event capacity does not exceed venue capacity
            Integer venueCapacity = existingEvent.getVenue().getVenueCapacity();
            if (venueCapacity < existingEvent.getCapacity()) {
                throw new VisibleException("Event capacity must not exceed venue capacity", HttpServletResponse.SC_BAD_REQUEST);
            }

            // Use the EventMapper to update the event in the database
            Event updatedEvent = eventMapper.update(existingEvent);

            //Update club balance
            newEventClub.setBalance(newEventClub.getBalance() - costIncrease);
            clubMapper.update(newEventClub);
            System.out.println("Thread " + Thread.currentThread().getName() + " updated event " + eventId + " successfully");

            return updatedEvent;
        }
        finally {
            // Release the locks
            lockManager.releaseLock(new Event(), eventId.toString(), username);
            lockManager.releaseLock(new Club(), eventRequest.getClubId().toString(), username);
        }
    }

    public Event cancelEvent(BigInteger eventId) {
        // Set var first so finally block can release the event object from map
        Event existingEvent = null;
        try {
            // Retrieve the existing event
            existingEvent = eventMapper.getEvent(new Event(eventId));
            // Event does not exist.
            if (existingEvent == null) {
                throw new VisibleException("Event with ID " + eventId + " does not exist.", HttpServletResponse.SC_NOT_FOUND);
            }
            lockManager.acquireLock(new Club(existingEvent.getClubId()), existingEvent.getClubId().toString(), Thread.currentThread().getName());
            lockManager.acquireLock(new Event(eventId), eventId.toString(), Thread.currentThread().getName());


            // Set the event as cancelled
            existingEvent.setCancelled(true);

            // Use the EventMapper to update the isCancelled field in the database
            Event cancelledEvent = eventMapper.cancelEvent(existingEvent);

            //Refund event cost to club
            Club eventClub = new Club(existingEvent.getClubId());
            eventClub.setBalance(eventClub.getBalance() + existingEvent.getCost());

            clubMapper.update(eventClub);

            return cancelledEvent;
        } finally {
            lockManager.releaseLock(new Event(eventId), eventId.toString(), Thread.currentThread().getName());
            lockManager.releaseLock(new Club(existingEvent.getClubId()), existingEvent.getClubId().toString(), Thread.currentThread().getName());

        }
    }


    public void deleteEvent(BigInteger eventId) {
        // Set var first so finally block can release the event object from map
        Event existingEvent = null;
        try{
            existingEvent = eventMapper.getEvent(new Event(eventId));
            if (existingEvent == null) {
                throw new VisibleException("Event with ID " + eventId + " does not exist.", HttpServletResponse.SC_NOT_FOUND);
            }
            lockManager.acquireLock(new Event(eventId), eventId.toString(), Thread.currentThread().getName());
            lockManager.acquireLock(new Club(existingEvent.getClubId()), existingEvent.getClubId().toString(), Thread.currentThread().getName());


            //Refund event cost to club if not already cancelled
            if (!existingEvent.isCancelled()) {
                Club eventClub = new Club(existingEvent.getClubId());

                eventClub.setBalance(eventClub.getBalance() + existingEvent.getCost());

                clubMapper.update(eventClub);
            }
            eventMapper.delete(existingEvent);
        } finally {
            lockManager.releaseLock(new Event(eventId), eventId.toString(), Thread.currentThread().getName());
            lockManager.releaseLock(new Club(existingEvent.getClubId()), existingEvent.getClubId().toString(), Thread.currentThread().getName());
        }
    }


    // Method to filter events based on the EventFilterRequest parameters
    public List<Event> getFilteredEvents(EventFilterRequest filterRequest) {
        // Validate the filter parameters
        if (filterRequest == null) {
            throw new VisibleException("Filter request cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // If no filters are applied, return an empty list or handle as needed
        if (filterRequest.getNameQuery() == null && filterRequest.getUpcoming() == null
                && filterRequest.getOnline() == null && filterRequest.getCancelled() == null) {
            throw new VisibleException("At least one filter condition must be provided.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Validate individual filters as needed, e.g., isUpcoming, isOnline, etc.
        if (filterRequest.getNameQuery() != null && filterRequest.getNameQuery().isEmpty()) {
            throw new VisibleException("Name query cannot be an empty string.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Call the EventMapper's method to apply filters and retrieve events
        List<Event> filteredEvents = eventMapper.getFilteredEvents(filterRequest);

        return filteredEvents;
    }

    // Helper functions
    private void validateRequest(EventRequest eventRequest){
        // Validate the input parameters
        if (eventRequest.getName() == null || eventRequest.getName().isEmpty()) {
            throw new VisibleException("Event name cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getClubId() == null) {
            throw new VisibleException("Club ID cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getIsOnline() == null) {
            throw new VisibleException("isOnline cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getVenueId() == null) {
            throw new VisibleException("Venue ID cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getCost() == null || eventRequest.getCost() < 0) {
            throw new VisibleException("Cost cannot be null or negative.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getCapacity() == null || eventRequest.getCapacity() < 1) {
            throw new VisibleException("Capacity must be at least 1.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getStartTime() == null || eventRequest.getEndTime() == null) {
            throw new VisibleException("Start time and end time cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventRequest.getEndTime().isBefore(eventRequest.getStartTime())) {
            throw new VisibleException("End time cannot be before start time.", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
