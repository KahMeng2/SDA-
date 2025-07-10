package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.Address;
import com.brogrammerbrigade.backend.domain.OnlineVenue;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OnlineVenueMapper implements Mapper<OnlineVenue> {
    private static OnlineVenueMapper instance;
    private final DatabaseManager databaseManager;

    private OnlineVenueMapper() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public static synchronized OnlineVenueMapper getInstance() {
        if (instance == null) {
            instance = new OnlineVenueMapper();
        }
        return instance;
    }

    public ArrayList<OnlineVenue> getAllOnlineVenues() {
        String SQL_GET_ALL = "SELECT * FROM app.online_venue";
        try {
            ResultSet results = databaseManager.executeQuery(SQL_GET_ALL, List.of());
            ArrayList<OnlineVenue> onlineVenues = new ArrayList<>();
            while (results.next()) {
                onlineVenues.add(this.mapVenue(results));
            }
            return onlineVenues;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all online venues: " + e.getMessage(), e);
        }
    }

    public OnlineVenue getOnlineVenueById(BigInteger id) {
        String SQL_GET_BY_ID = "SELECT * FROM app.online_venue WHERE id = ?";
        try {
            List<Object> parameters = List.of(id);
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_BY_ID, parameters);
            if (resultSet.next()) {
                return this.mapVenue(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve online venue by ID: " + e.getMessage(), e);
        }
    }

    public OnlineVenue insert(OnlineVenue onlineVenue) {
        String SQL_INSERT = "INSERT INTO app.online_venue (description, cost, venue_capacity, link) VALUES (?,?,?,?);";
        try {
            List<Object> parameters = List.of(
                    onlineVenue.getDescription(),
                    onlineVenue.getCost(),
                    onlineVenue.getVenueCapacity(),
                    onlineVenue.getLink()
            );
            BigInteger generatedId = databaseManager.executeUpdate(SQL_INSERT, parameters, true);
            if (generatedId == null) {
                throw new SQLException("Creating online venue failed, no ID obtained.");
            }
            onlineVenue.setId(generatedId);
            return onlineVenue;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create online venue: " + e.getMessage(), e);
        }
    }

    public OnlineVenue update(OnlineVenue onlineVenue) {
        // TODO implementation
        return null;
    }


    public void delete(OnlineVenue onlineVenue) {
        BigInteger onlineVenueId = onlineVenue.getId();
        String SQL_DELETE = "DELETE FROM app.online_venue WHERE id = ?;";
        try {
            List<Object> parameters = List.of(onlineVenueId);
            databaseManager.executeUpdate(SQL_DELETE, parameters, false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete online venue: " + e.getMessage(), e);
        }
    }

    public Boolean exists(OnlineVenue onlineVenue) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.online_venue WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(onlineVenue.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check venue existence: %s", e.getMessage()), e);
        }

        return output;
    }

    private OnlineVenue mapVenue(ResultSet resultSet) throws SQLException {
        OnlineVenue onlineVenue = new OnlineVenue(BigInteger.valueOf(resultSet.getLong("id")));
        onlineVenue.setDescription(resultSet.getString("description"));
        onlineVenue.setCost(resultSet.getDouble("cost"));
        onlineVenue.setVenueCapacity(resultSet.getInt("venue_capacity"));
        onlineVenue.setLink(resultSet.getString("link"));
        return onlineVenue;
    }
}