package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.*;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class RsvpMapper implements Mapper<Rsvp> {
    private static RsvpMapper instance;
    private final DatabaseManager databaseManager;

    private RsvpMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    // Public method to provide access to the singleton instance
    public static synchronized RsvpMapper getInstance() {
        if (instance == null) {
            instance = new RsvpMapper();
        }
        return instance;
    }

    public Rsvp getRsvp(Student rsvpStudent, Event event) {
        BigInteger rsvpStudentId = rsvpStudent.getId();
        BigInteger eventId = event.getId();
        String SQL_GET_RSVP = "SELECT * FROM app.rsvp WHERE RSVP_student_id = ? and event_id = ?";
        try {
            List<Object> parameters = List.of(rsvpStudentId, eventId); // Pass the rsvp id as a parameter
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_RSVP, parameters);

            // If a result is found, map it to a Rsvp object

            if (resultSet.next()) {
                return this.map(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve rsvp by ID: %s", e.getMessage()), e);
        }
    }

    public List<Rsvp> getRsvpsForEvent(Event event) {
        BigInteger eventId = event.getId();
        String SQL_GET_FOR_EVENT = "SELECT * FROM app.rsvp where event_id = ?";
        try {
            List<Object> parameters = List.of(eventId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_EVENT, parameters);
            ArrayList<Rsvp> rsvps = new ArrayList<>();
            while (results.next()) {
                rsvps.add(this.map(results));
            }
            return rsvps;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Rsvp> getRsvpsForStudent(BigInteger rsvpStudentId) {
        String SQL_GET_FOR_STUDENT = "SELECT * FROM app.rsvp where RSVP_student_id = ?";
        try {
            List<Object> parameters = List.of(rsvpStudentId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_STUDENT, parameters);
            ArrayList<Rsvp> rsvps = new ArrayList<>();
            while (results.next()) {
                rsvps.add(this.map(results));
            }
            return rsvps;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Rsvp insert(Rsvp rsvp) {
        String SQL_INSERT_RSVP = "INSERT INTO app.rsvp (RSVP_student_id, event_id, date_created) " +
                "VALUES (?, ?, ?);";

        try {
            // Insert the RSVP
            List<Object> rsvpParameters = List.of(
                    rsvp.getRsvpStudentId(),
                    rsvp.getEventId(),
                    rsvp.getDateCreated()
            );
            databaseManager.executeUpdate(SQL_INSERT_RSVP, rsvpParameters, false);

            // Return the created RSVP
            return rsvp;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle duplicate RSVP (primary key violation)
            throw new VisibleException("RSVP already exists for the given student and event.", HttpServletResponse.SC_FORBIDDEN);
        } catch (SQLException e) {
            // Handle other SQL exceptions
            throw new RuntimeException(String.format("Failed to create RSVP: %s", e.getMessage()), e);
        }
    }

    public Rsvp update(Rsvp rsvp) {
        String SQL_UPDATE_RSVP = "UPDATE app.rsvp " +
                "SET date_created = ? " +
                "WHERE RSVP_student_id = ? AND event_id = ?;";

        try {
            // Update the RSVP
            List<Object> updateParameters = List.of(
                    rsvp.getDateCreated(),       // New date_created value
                    rsvp.getRsvpStudentId(),     // RSVP_student_id for WHERE clause
                    rsvp.getEventId()            // event_id for WHERE clause
            );
            databaseManager.executeUpdate(SQL_UPDATE_RSVP, updateParameters, false);

            // Return the updated RSVP object
            return rsvp;

        } catch (SQLException e) {
            // Handle SQL exceptions
            throw new RuntimeException(String.format("Failed to update RSVP: %s", e.getMessage()), e);
        }
    }



    public void delete(Rsvp rsvp) {
        String SQL_DELETE_RSVP = "DELETE FROM app.rsvp WHERE RSVP_student_id = ? AND event_id = ?;";
        BigInteger rsvpStudentId = rsvp.getRsvpStudentId();
        BigInteger eventId = rsvp.getEventId();

        try {
            List<Object> deleteParameters = List.of(rsvpStudentId, eventId);
            databaseManager.executeUpdate(SQL_DELETE_RSVP, deleteParameters, false);

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to delete RSVP: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(Rsvp rsvp) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.RSVP WHERE RSVP_student_id = ? AND event_id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(rsvp.getRsvpStudentId(), rsvp.getEventId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to add rsvp: %s", e.getMessage()), e);
        }

        return output;
    }

    // Helper function to map ResultSet to Rsvp object
    private Rsvp map(ResultSet resultSet) throws SQLException {
        // set rsvp student
        BigInteger eventId = BigInteger.valueOf(resultSet.getLong("event_id"));
        BigInteger rsvpStudentId = BigInteger.valueOf(resultSet.getLong("RSVP_student_id"));
        Rsvp rsvp = new Rsvp(rsvpStudentId, eventId);

        // Retrieve timestamp and convert it to OffsetDateTime
        Timestamp timestamp = resultSet.getTimestamp("date_created");
        if (timestamp != null) {
            OffsetDateTime offsetDateTime = timestamp.toInstant().atOffset(ZoneOffset.UTC);
            rsvp.setDateCreated(offsetDateTime);
        }

        return rsvp;
    }
}
