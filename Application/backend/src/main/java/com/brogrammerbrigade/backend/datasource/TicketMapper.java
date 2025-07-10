package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.*;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketMapper implements Mapper<Ticket> {
    private static TicketMapper instance;
    private final DatabaseManager databaseManager;

    private TicketMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    public List<Ticket> getTicketsForRsvp(Rsvp rsvp) {
        BigInteger rsvpStudentId = rsvp.getRsvpStudentId();
        BigInteger eventId = rsvp.getEventId();

        String SQL_GET_FOR_RSVP = "SELECT * FROM app.ticket where RSVP_student_id = ? and event_id = ?;";

        try {
            List<Object> parameters = List.of(rsvpStudentId, eventId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_RSVP, parameters);
            ArrayList<Ticket> tickets = new ArrayList<>();
            while (results.next()) {
                tickets.add(this.map(results));
            }
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ticket getTicket(Ticket ticket) {
        BigInteger ticketStudentId = ticket.getTicketStudentId();
        BigInteger eventId = ticket.getEventId();
        System.out.println("mapper rsvpStudentId: " + ticketStudentId + " mapper eventId: " + eventId);

        String SQL_GET_FOR_RSVP = "SELECT * FROM app.ticket where ticket_student_id = ? and event_id = ?;";
        try {
            List<Object> parameters = List.of(ticketStudentId, eventId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_RSVP, parameters);

            if (results.next()) {
                return this.map(results);
            } else {
                throw new RuntimeException("Ticket does not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ticket> getTicketsForStudent(Student student) {
        BigInteger studentId = student.getId();

        String SQL_GET_FOR_STUDENT = "SELECT * FROM app.ticket where ticket_student_id = ?;";

        try {
            List<Object> parameters = List.of(studentId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_STUDENT, parameters);
            ArrayList<Ticket> tickets = new ArrayList<>();
            while (results.next()) {
                tickets.add(this.map(results));
            }
            return tickets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Public method to provide access to the singleton instance
    public static synchronized TicketMapper getInstance() {
        if (instance == null) {
            instance = new TicketMapper();
        }
        return instance;
    }

    public Ticket insert(Ticket ticket) {
        String SQL_INSERT_TICKET = "INSERT INTO app.ticket (event_id, RSVP_student_id, ticket_student_id, special_preferences) " +
                "VALUES (?, ?, ?, ?);";

        try {
            // Insert the RSVP
            List<Object> rsvpParameters = List.of(
                    ticket.getEventId(),
                    ticket.getRsvpStudentId(),
                    ticket.getTicketStudentId(),
                    ticket.getSpecialPreferences()
            );
            databaseManager.executeUpdate(SQL_INSERT_TICKET, rsvpParameters, false);

            // Return the created RSVP
            return ticket;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate RSVP (primary key violation)
            throw new VisibleException("Ticket already exists for the given student and event.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            // Handle other SQL exceptions
            throw new RuntimeException(String.format("Failed to create RSVP: %s", e.getMessage()), e);
        }
    }

    public Ticket update(Ticket ticket) {
        String SQL_UPDATE_TICKET = "UPDATE app.ticket " +
                "SET special_preferences = ? " +
                "WHERE event_id = ? AND RSVP_student_id = ? AND ticket_student_id = ?;";

        try {
            // Update the ticket
            List<Object> updateParameters = new ArrayList<>();
            updateParameters.add(ticket.getSpecialPreferences()); // May be null
            updateParameters.add(ticket.getEventId());
            updateParameters.add(ticket.getRsvpStudentId());
            updateParameters.add(ticket.getTicketStudentId());

            databaseManager.executeUpdate(SQL_UPDATE_TICKET, updateParameters, false);

            // Return the updated ticket object
            return ticket;

        } catch (SQLException e) {
            // Handle SQL exceptions
            throw new RuntimeException(String.format("Failed to update Ticket: %s", e.getMessage()), e);
        }
    }

    public void delete(Ticket ticket) {
        String SQL_DELETE_TICKET = "DELETE FROM app.ticket " +
                "WHERE event_id = ? AND RSVP_student_id = ? AND ticket_student_id = ?;";

        try {
            // Prepare parameters for the DELETE statement
            List<Object> deleteParameters = List.of(
                    ticket.getEventId(),       // Event ID (for WHERE clause)
                    ticket.getRsvpStudentId(), // RSVP student ID (for WHERE clause)
                    ticket.getTicketStudentId() // Ticket student ID (for WHERE clause)
            );

            // Execute the DELETE statement
            databaseManager.executeUpdate(SQL_DELETE_TICKET, deleteParameters, false);

        } catch (SQLException e) {
            // Handle SQL exceptions
            throw new RuntimeException(String.format("Failed to delete Ticket: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(Ticket ticket) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.ticket " +
                "WHERE event_id = ? AND ticket_student_id = ?;";
        Boolean output = null;

        try {
            List<Object> existsParameters = List.of(
                    ticket.getEventId(),
                    ticket.getTicketStudentId()
            );
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, existsParameters);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to add club: %s", e.getMessage()), e);
        }

        return output;
    }


    private Ticket map(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketStudent(new Student(BigInteger.valueOf(rs.getLong("ticket_student_id"))));
        ticket.setEvent(new Event(BigInteger.valueOf(rs.getLong("event_id"))));
        ticket.setRsvpStudent(new Student(BigInteger.valueOf(rs.getLong("RSVP_student_id"))));
        ticket.setSpecialPreferences(rs.getString("special_preferences"));

        return ticket;
    }
}
