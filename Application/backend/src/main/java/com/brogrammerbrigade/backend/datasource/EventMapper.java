package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.*;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import com.brogrammerbrigade.backend.dto.EventFilterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class EventMapper implements Mapper<Event> {
    private static EventMapper instance;
    private final DatabaseManager databaseManager;

    private EventMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    // Public method to provide access to the singleton instance
    public static synchronized EventMapper getInstance() {
        if (instance == null) {
            instance = new EventMapper();
        }
        return instance;
    }

    public ArrayList<Event> getAllEvents() {
        String SQL_GET_ALL = "SELECT * FROM app.event";
        try {
            ResultSet results = databaseManager.executeQuery(SQL_GET_ALL, List.of());
            ArrayList<Event> events = new ArrayList<>();
            while (results.next()) {
                events.add(this.map(results));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Event getEvent(Event event) {
        BigInteger id = event.getId();
        String SQL_GET_BY_ID = "SELECT * FROM app.event WHERE id = ?";
        try {
            List<Object> parameters = List.of(id);
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_BY_ID, parameters);

            if (resultSet.next()) {
                return this.map(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve event by ID: %s", e.getMessage()), e);
        }
    }

    public List<Event> getEventsForClub(Club club) {
        if (!ClubMapper.getInstance().exists(club)) {
            throw new VisibleException(String.format("Club with id %s does not exist", club.getId()), HttpServletResponse.SC_NOT_FOUND);
        }

        BigInteger clubId = club.getId();
        String SQL_GET_FOR_CLUB = "SELECT * FROM app.event where club_id = ?";
        try {
            List<Object> parameters = List.of(clubId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_CLUB, parameters);
            ArrayList<Event> events = new ArrayList<>();
            while (results.next()) {
                events.add(this.map(results));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> getEventsForVenue(BigInteger venueId, Boolean isOnline) {
        // Adjust SQL query based on whether the venue is online or physical
        String SQL_GET_FOR_VENUE;
        if (isOnline) {
            SQL_GET_FOR_VENUE = "SELECT * FROM app.event WHERE online_venue_id = ?";
        } else {
            SQL_GET_FOR_VENUE = "SELECT * FROM app.event WHERE physical_venue_id = ?";
        }

        try {
            List<Object> parameters = List.of(venueId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_VENUE, parameters);
            ArrayList<Event> events = new ArrayList<>();
            while (results.next()) {
                events.add(this.map(results));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Event insert(Event event) {
        String SQL_INSERT_EVENT_ONLINE = "INSERT INTO app.event (club_id, is_cancelled, is_online, online_venue_id, name, description, start_time, end_time, cost, num_tickets, event_capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String SQL_INSERT_EVENT_PHYSICAL = "INSERT INTO app.event (club_id, is_cancelled, is_online, physical_venue_id, name, description, start_time, end_time, cost, num_tickets, event_capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        // Prepare the parameters for the SQL insert statement
        List<Object> parameters = List.of(
                event.getClubId(),
                event.isCancelled(),
                event.isOnline(),
                event.getVenueId(),  // This will either be online_venue_id or physical_venue_id
                event.getName(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCost(),
                event.getNumTickets(),
                event.getCapacity()
        );

        // Insert event into the appropriate venue table (online or physical)
        String SQL_INSERT_EVENT = event.isOnline() ? SQL_INSERT_EVENT_ONLINE : SQL_INSERT_EVENT_PHYSICAL;

        try {
            // Execute the SQL insert and obtain the generated event ID
            BigInteger generatedId = databaseManager.executeUpdate(SQL_INSERT_EVENT, parameters, true);
            if (generatedId == null) {
                throw new SQLException("Creating event failed, no ID obtained.");
            }

            // Set the generated BigInteger ID in the Event object
            event.setId(generatedId);
            return event;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to create event: %s", e.getMessage()), e);
        }
    }

    public Event update(Event event) {
        String SQL_UPDATE_EVENT_ONLINE = "UPDATE app.event SET club_id = ?, is_cancelled = ?, is_online = ?, online_venue_id = ?, name = ?, description = ?, start_time = ?, end_time = ?, cost = ?, num_tickets = ?, event_capacity = ? WHERE id = ?;";
        String SQL_UPDATE_EVENT_PHYSICAL = "UPDATE app.event SET club_id = ?, is_cancelled = ?, is_online = ?, physical_venue_id = ?, name = ?, description = ?, start_time = ?, end_time = ?, cost = ?, num_tickets = ?, event_capacity = ? WHERE id = ?;";

        // Prepare the parameters for the SQL update statement
        List<Object> parameters = List.of(
                event.getClubId(),
                event.isCancelled(),
                event.isOnline(),
                event.getVenueId(),  // This will either be online_venue_id or physical_venue_id
                event.getName(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCost(),
                event.getNumTickets(),
                event.getCapacity(),
                event.getId()  // The event ID is needed to update the correct record
        );

        // Update event in the appropriate venue table (online or physical)
        String SQL_UPDATE_EVENT = event.isOnline() ? SQL_UPDATE_EVENT_ONLINE : SQL_UPDATE_EVENT_PHYSICAL;

        try {
            // Execute the SQL update
            databaseManager.executeUpdate(SQL_UPDATE_EVENT, parameters, false);

            return event;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to update event: %s", e.getMessage()), e);
        }
    }

    public void delete(Event event) {
        String SQL_DELETE_EVENT = "DELETE FROM app.event WHERE id = ?;";
        BigInteger eventId = event.getId();

        if (!exists(event)) {
            throw new VisibleException("Event with id " + event.getId() + " does not exist", HttpServletResponse.SC_NOT_FOUND);
        }

        try {
            // delete event
            databaseManager.executeUpdate(SQL_DELETE_EVENT, List.of(eventId), false);

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to delete event: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(Event event) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.event WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(event.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check event existence: %s", e.getMessage()), e);
        }

        return output;
    }

    public Event getClash(Event event) {
        String SQL_CLASHES = """
        SELECT * FROM app.event
        WHERE ((physical_venue_id = ? AND ? = false) OR (online_venue_id = ? AND ? = true))
        AND is_cancelled = false
        %s
        AND (start_time < ? AND end_time > ?)
        LIMIT 1;
    """;

        try {
            List<Object> params;
            String idClause = "";

            // If the event has an ID, add the exclusion condition to the query
            if (event.getId() != null) {
                idClause = "AND id != ?";
                params = List.of(
                        event.getVenueId(),
                        event.isOnline(),
                        event.getVenueId(),
                        event.isOnline(),
                        event.getId(),
                        event.getEndTime(),
                        event.getStartTime()
                );
            } else {
                // No ID for a new event
                params = List.of(
                        event.getVenueId(),
                        event.isOnline(),
                        event.getVenueId(),
                        event.isOnline(),
                        event.getEndTime(),
                        event.getStartTime()
                );
            }

            // Format the SQL query with the optional idClause
            String finalSQL = String.format(SQL_CLASHES, idClause);

            // Execute the query
            ResultSet resultSet = databaseManager.executeQuery(finalSQL, params);

            if (resultSet.next()) {
                return this.map(resultSet);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check event clashes: %s", e.getMessage()), e);
        }
    }


    public Event cancelEvent(Event event) {
        String SQL_CANCEL_EVENT = "UPDATE app.event SET is_cancelled = ? WHERE id = ?;";

        try {
            // Prepare the parameters for the SQL update statement
            List<Object> parameters = List.of(
                    event.isCancelled(),  // Should be set to true
                    event.getId()         // Event ID to identify the event to be cancelled
            );

            // Execute the SQL update to set isCancelled to true
            databaseManager.executeUpdate(SQL_CANCEL_EVENT, parameters, false);

            return event;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel event: %s", e.getMessage()), e);
        }
    }


    public ArrayList<Event> getFilteredEvents(EventFilterRequest filterRequest) {
        // Base SQL query
        StringBuilder query = new StringBuilder("SELECT * FROM app.event WHERE 1=1");
        ArrayList<Object> parameters = new ArrayList<>();

        // Dynamically build the query based on the filters provided
        if (filterRequest.getNameQuery() != null && !filterRequest.getNameQuery().isEmpty()) {
            query.append(" AND name ILIKE ?");
            parameters.add("%" + filterRequest.getNameQuery() + "%");
        }

        if (filterRequest.getUpcoming() != null && filterRequest.getUpcoming()) {
            query.append(" AND start_time > NOW()"); // Assuming you have a start_time column
        }

        if (filterRequest.getOnline() != null) {
            query.append(" AND is_online = ?");
            parameters.add(filterRequest.getOnline());
        }

        if (filterRequest.getCancelled() != null) {
            query.append(" AND is_cancelled = ?");
            parameters.add(filterRequest.getCancelled());
        }

        // Execute the query
        try {
            ResultSet results = databaseManager.executeQuery(query.toString(), parameters);
            ArrayList<Event> events = new ArrayList<>();

            // Map the result set to Event objects
            while (results.next()) {
                events.add(this.map(results));
            }

            return events;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered events: " + e.getMessage(), e);
        }
    }


    // Helper function to map ResultSet to Event object
    private Event map(ResultSet resultSet) throws SQLException {
        Event event = new Event();
        event.setId(BigInteger.valueOf(resultSet.getLong("id")));
        Club eventClub = new Club(BigInteger.valueOf(resultSet.getLong("club_id")));
        event.setClub(eventClub);
        event.setCost(resultSet.getDouble("cost"));
        event.setCapacity(resultSet.getInt("event_capacity"));
        event.setNumTickets(resultSet.getInt("num_tickets"));
        event.setName(resultSet.getString("name"));
        event.setDescription(resultSet.getString("description"));
        event.setCancelled(resultSet.getBoolean("is_cancelled"));


        event.setOnline(resultSet.getBoolean("is_online"));
        if (event.isOnline()) {
            event.setOnlineVenueId(BigInteger.valueOf(resultSet.getLong("online_venue_id")));
        } else {
            event.setPhysicalVenueId(BigInteger.valueOf(resultSet.getLong("physical_venue_id")));
        }

        // add start and end times
        Timestamp startTimestamp = resultSet.getTimestamp("start_time");
        if (startTimestamp != null) {
            OffsetDateTime offsetStartDateTime = startTimestamp.toInstant().atOffset(ZoneOffset.UTC);
            event.setStartTime(offsetStartDateTime);
        }

        Timestamp endTimestamp = resultSet.getTimestamp("end_time");
        if (endTimestamp != null) {
            OffsetDateTime offsetEndDateTime = endTimestamp.toInstant().atOffset(ZoneOffset.UTC);
            event.setEndTime(offsetEndDateTime);
        }


        return event;
    }
}
