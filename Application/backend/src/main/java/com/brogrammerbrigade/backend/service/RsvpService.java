package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.*;
import com.brogrammerbrigade.backend.datasource.RsvpMapper;
import com.brogrammerbrigade.backend.domain.*;
import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.dto.RsvpRequest;
import com.brogrammerbrigade.backend.dto.TicketRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.LockManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RsvpService {
    private static RsvpService instance;
    private final RsvpMapper rsvpMapper;
    private final TicketMapper ticketMapper;
    private final EventMapper eventMapper;
    private final LockManager lockManager = LockManager.getInstance();

    private RsvpService() {
        this.rsvpMapper = RsvpMapper.getInstance();
        this.ticketMapper = TicketMapper.getInstance();
        this.eventMapper = EventMapper.getInstance();
    }

    // Public method to provide access to the singleton instance
    public static synchronized RsvpService getInstance() {
        if (instance == null) {
            instance = new RsvpService();
        }
        return instance;
    }

    public Rsvp getRsvp(BigInteger rsvpStudentId, BigInteger eventId) {
        if (rsvpStudentId == null) {
            throw new VisibleException("rsvpStudentId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (eventId == null) {
            throw new VisibleException("eventId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        return rsvpMapper.getRsvp(new Student(rsvpStudentId), new Event(eventId));
    }

    public List<Rsvp> getRsvpsForEvent(BigInteger eventId) {
        if (eventId == null) {
            throw new VisibleException("eventId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        Event event = new Event(eventId);
        return rsvpMapper.getRsvpsForEvent(event);
    }

    public List<Rsvp> getRsvpsForStudent(BigInteger rsvpStudentId) {
        if (rsvpStudentId == null) {
            throw new VisibleException("rsvpStudentId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        return rsvpMapper.getRsvpsForStudent(rsvpStudentId);
    }

    public List<Ticket> getTicketsForStudent(BigInteger studentId) {
        if (studentId == null) {
            throw new VisibleException("studentId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        Student student = new Student(studentId);
        return student.getTickets();
    }
    
    // Method to create a new Rsvp with validation
    public Rsvp createRsvp(RsvpRequest rsvpRequest) {
        BigInteger rsvpStudentId = rsvpRequest.getRsvpStudentId();
        BigInteger eventId = rsvpRequest.getEventId();
        List<TicketRequest> ticketRequests = rsvpRequest.getTicketRequests();

        try {
            // Gets all locks for values that will be updated.
            // Locks event
            lockManager.acquireLock(new Event(), eventId.toString(), Thread.currentThread().getName());
            // Locks RSVP
            lockManager.acquireLock(new Rsvp(), rsvpStudentId.toString() + eventId.toString(), Thread.currentThread().getName());

            // Fails b4 queries are done, and is functionally identical to optimistic
            for (TicketRequest ticketRequest : ticketRequests) {
                BigInteger ticketStudentId = ticketRequest.getTicketStudentId();
                // Identifying key is ticket student id and the event ID
                lockManager.acquireLock(new Ticket(), ticketStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
            }



            // Validate the input parameters
            if (rsvpStudentId == null) {
                throw new VisibleException("rsvpStudentId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
            }
            if (eventId == null) {
                throw new VisibleException("eventId field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
            }
            if (ticketRequests == null) {
                throw new VisibleException("tickets field cannot be null", HttpServletResponse.SC_BAD_REQUEST);
            }

            // check that rsvp student has not already created an rsvp for this event
            Student rsvpStudent = new Student(rsvpStudentId);
            Event rsvpEvent = new Event(eventId);
            if (!(rsvpMapper.getRsvp(rsvpStudent, rsvpEvent) == null)) {
                throw new VisibleException("Student " + rsvpStudentId + " already made an RSVP for this event", HttpServletResponse.SC_CONFLICT);
            }

            // Create the Rsvp object
            Rsvp newRsvp = new Rsvp();

            newRsvp.setRsvpStudentId(rsvpStudentId);
            newRsvp.setEventId(eventId);
            newRsvp.setDateCreated(OffsetDateTime.now());

            List<Ticket> tickets = new ArrayList<>();
            for (TicketRequest ticketRequest : ticketRequests) {
                BigInteger ticketStudentId = ticketRequest.getTicketStudentId();
                Ticket ticket = new Ticket(eventId, rsvpStudentId, ticketStudentId);

                // check that ticket student has not been rsvped already
                if (ticketMapper.exists(ticket)) {
                    throw new VisibleException("Student " + ticketStudentId + " already has a ticket for this event", HttpServletResponse.SC_CONFLICT);
                }

                ticket.setSpecialPreferences(ticketRequest.getSpecialPreferences());
                tickets.add(ticket);
            }

            // check that rsvps do not exceed event capacity
            Integer ticketIncrement = tickets.size();
            Integer ticketsLeft = rsvpEvent.getCapacity() - rsvpEvent.getNumTickets();
            if (ticketIncrement > ticketsLeft) {
                throw new VisibleException("RSVP exceeds event capacity -only " + ticketsLeft + " tickets left", HttpServletResponse.SC_BAD_REQUEST);
            }

            // Rsvp must be inserted before ticket due to foreign key constraints
            Rsvp insertedRsvp = rsvpMapper.insert(newRsvp);
            for (Ticket ticket : tickets) {
                ticketMapper.insert(ticket);
            }



            // update numTickets in event
            rsvpEvent.setNumTickets(rsvpEvent.getNumTickets() + ticketIncrement);
            eventMapper.update(rsvpEvent);

            return insertedRsvp;
        } finally {
            for (TicketRequest ticketRequest : ticketRequests) {
                BigInteger ticketStudentId = ticketRequest.getTicketStudentId();
                // Identifying key is ticket student id and the event ID
                lockManager.releaseLock(new Ticket(), ticketStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
            }
            // releases RSVP
            lockManager.releaseLock(new Rsvp(), rsvpStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
            // releases event
            lockManager.releaseLock(new Event(), eventId.toString(), Thread.currentThread().getName());
        }
    }

    public Rsvp updateRsvpTickets(RsvpRequest rsvpRequest) {
        BigInteger rsvpStudentId = rsvpRequest.getRsvpStudentId();
        BigInteger eventId = rsvpRequest.getEventId();
        List<TicketRequest> incomingTickets = rsvpRequest.getTicketRequests();
        Event rsvpEvent = new Event(eventId);
        Student rsvpStudent = new Student(rsvpStudentId);

        // Validate input parameters
        if (rsvpStudentId == null || eventId == null || incomingTickets == null) {
            throw new VisibleException("Invalid input parameters", HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            // Acquire locks for all resources that will be updated
            // Lock the event
            lockManager.acquireLock(new Event(), eventId.toString(), Thread.currentThread().getName());
            // Lock the RSVP
            lockManager.acquireLock(new Rsvp(), rsvpStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
            // Lock all potential tickets (max 3)
            for (TicketRequest ticketRequest : incomingTickets) {
                BigInteger ticketStudentId = ticketRequest.getTicketStudentId();
                lockManager.acquireLock(new Ticket(), ticketStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
            }

            // Fetch the existing RSVP from the database
            Rsvp existingRsvp = rsvpMapper.getRsvp(rsvpStudent, rsvpEvent);
            if (existingRsvp == null) {
                throw new IllegalArgumentException("RSVP not found");
            }

            // Fetch existing tickets for comparison
            List<Ticket> existingTickets = ticketMapper.getTicketsForRsvp(existingRsvp);
            Integer numExistingTickets = existingTickets.size();
            Integer numIncomingTickets = incomingTickets.size();

            // check that event capacity is not exceeded
            Integer ticketsLeft = rsvpEvent.getCapacity() - (rsvpEvent.getNumTickets() - numExistingTickets);
            if (rsvpEvent.getNumTickets() + (numIncomingTickets - numExistingTickets) > rsvpEvent.getCapacity()) {
                throw new VisibleException("RSVP exceeds capacity - only " + ticketsLeft + " tickets left", HttpServletResponse.SC_BAD_REQUEST);
            }

            List<Ticket> ticketsToDelete = new ArrayList<>(existingTickets); // Copy existing tickets to check for deletion

            // Get current UnitOfWork instance
            UnitOfWork.newCurrent();

            // Process the incoming tickets
            for (TicketRequest incomingTicketRequest : incomingTickets) {
                Ticket matchingTicket = findMatchingTicket(existingTickets, incomingTicketRequest.getTicketStudentId());

                if (matchingTicket == null) {
                    // This is a new ticket (not in existing tickets)
                    BigInteger newTicketStudentId = incomingTicketRequest.getTicketStudentId();
                    Ticket newTicket = new Ticket(eventId, rsvpStudentId, newTicketStudentId);
                    if (ticketMapper.exists(newTicket)) {
                        throw new VisibleException("Student " + newTicketStudentId + " already has a ticket for this event", HttpServletResponse.SC_CONFLICT);
                    }
                    newTicket.setSpecialPreferences(incomingTicketRequest.getSpecialPreferences());

                    UnitOfWork.getCurrent().registerNew(newTicket);

                } else {
                    // The ticket exists, check if specialPreferences have changed
                    if (!Objects.equals(matchingTicket.getSpecialPreferences(), incomingTicketRequest.getSpecialPreferences())) {
                        // Ticket is dirty because specialPreferences have changed
                        matchingTicket.setSpecialPreferences(incomingTicketRequest.getSpecialPreferences());
                        UnitOfWork.getCurrent().registerDirty(matchingTicket);
                    }
                    // Remove the processed ticket from ticketsToDelete (we don't want to delete it)
                    ticketsToDelete.remove(matchingTicket);
                }
            }

            // Any remaining tickets in ticketsToDelete are no longer present in the incoming request, so delete them
            for (Ticket ticketToDelete : ticketsToDelete) {
                UnitOfWork.getCurrent().registerDeleted(ticketToDelete);
            }

            // Commit the UnitOfWork
            UnitOfWork.getCurrent().commit();

            // Return the updated RSVP, including its updated list of tickets
            Rsvp updatedRsvp = rsvpMapper.getRsvp(new Student(rsvpStudentId), new Event(eventId));
            List<Ticket> newTickets = ticketMapper.getTicketsForRsvp(updatedRsvp);
            updatedRsvp.setTickets(newTickets);

            // update numTickets in event
            Integer ticketIncrement = newTickets.size() - existingTickets.size();
            rsvpEvent.setNumTickets(rsvpEvent.getNumTickets() + ticketIncrement);
            eventMapper.update(rsvpEvent);

            return updatedRsvp;

        } catch (VisibleException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Error updating RSVP", e);
        } finally {
            // Release all locks
            try {
                lockManager.releaseLock(new Event(), eventId.toString(), Thread.currentThread().getName());
                lockManager.releaseLock(new Rsvp(), rsvpStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
                for (TicketRequest ticketRequest : incomingTickets) {
                    BigInteger ticketStudentId = ticketRequest.getTicketStudentId();
                    lockManager.releaseLock(new Ticket(), ticketStudentId.toString() + eventId.toString(), Thread.currentThread().getName());
                }
            } catch (Exception e) {
                System.err.println("Error releasing locks: " + e.getMessage());
            }
            UnitOfWork.setCurrent(null);
        }
    }


    private Ticket findMatchingTicket(List<Ticket> existingTickets, BigInteger ticketStudentId) {
        for (Ticket ticket : existingTickets) {
            if (ticket.getTicketStudentId().equals(ticketStudentId)) {
                return ticket;
            }
        }
        return null; // No matching ticket found
    }



    public void deleteRsvp(BigInteger rsvpStudentId, BigInteger eventId) {
        Rsvp rsvp = new Rsvp();
        rsvp.setRsvpStudentId(rsvpStudentId);
        rsvp.setEventId(eventId);

        // update numTickets in event
        List<Ticket> tickets = ticketMapper.getTicketsForRsvp(rsvp);

        // update numTickets in event
        Integer ticketDecrement = tickets.size();
        Event rsvpEvent = new Event(eventId);
        rsvpEvent.setNumTickets(rsvpEvent.getNumTickets() - ticketDecrement);
        eventMapper.update(rsvpEvent);

        rsvpMapper.delete(rsvp);
    }
}
