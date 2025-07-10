package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.Address;
import com.brogrammerbrigade.backend.domain.PhysicalVenue;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhysicalVenueMapper implements Mapper<PhysicalVenue> {
    private static PhysicalVenueMapper instance;
    private final DatabaseManager databaseManager;

    private PhysicalVenueMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    // Public method to provide access to the singleton instance
    public static synchronized PhysicalVenueMapper getInstance() {
        if (instance == null) {
            instance = new PhysicalVenueMapper();
        }
        return instance;
    }

    public ArrayList<PhysicalVenue> getAllPhysicalVenues() {
        String SQL_GET_ALL_VENUES = "SELECT * FROM app.physical_venue";
        String SQL_GET_ADDRESS_BY_ID = "SELECT * FROM app.address WHERE id = ?";

        try {
            ResultSet venueResults = databaseManager.executeQuery(SQL_GET_ALL_VENUES, List.of());
            ArrayList<PhysicalVenue> physicalVenues = new ArrayList<>();

            while (venueResults.next()) {
                PhysicalVenue venue = this.mapVenue(venueResults);

                // Fetch address for each venue
                BigInteger addressId = venueResults.getBigDecimal("address_id").toBigInteger();
                List<Object> addressParameters = List.of(addressId);
                ResultSet addressResultSet = databaseManager.executeQuery(SQL_GET_ADDRESS_BY_ID, addressParameters);

                if (addressResultSet.next()) {
                    Address address = AddressMapper.getInstance().map(addressResultSet);
                    venue.setAddress(address);
                }

                physicalVenues.add(venue);
            }

            return physicalVenues;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve all physical venues: " + e.getMessage(), e);
        }
    }

    public PhysicalVenue getPhysicalVenueById(BigInteger id) {
        String SQL_GET_VENUE_BY_ID = "SELECT * FROM app.physical_venue WHERE id = ?";
        String SQL_GET_ADDRESS_BY_ID = "SELECT * FROM app.address WHERE id = ?";

        try {
            List<Object> parameters = List.of(id);
            ResultSet venueResultSet = databaseManager.executeQuery(SQL_GET_VENUE_BY_ID, parameters);

            if (venueResultSet.next()) {
                PhysicalVenue venue = this.mapVenue(venueResultSet);

                // Fetch address separately
                BigInteger addressId = venueResultSet.getBigDecimal("address_id").toBigInteger();
                List<Object> addressParameters = List.of(addressId);
                ResultSet addressResultSet = databaseManager.executeQuery(SQL_GET_ADDRESS_BY_ID, addressParameters);

                if (addressResultSet.next()) {
                    Address address = AddressMapper.getInstance().map(addressResultSet);
                    venue.setAddress(address);
                }

                return venue;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Failed to retrieve physicalVenue by ID: %s", e.getMessage()), e);
        }
    }

    public PhysicalVenue insert(PhysicalVenue physicalVenue) {
        String SQL_INSERT_CLUB = "INSERT INTO app.physical_venue (description, cost, venue_capacity, address_id, " +
                "floor, room) VALUES (?,?,?,?,?,?);";
        try {
            List<Object> parameters = List.of(
                    physicalVenue.getDescription(),
                    physicalVenue.getCost(),  // This will now be a Double
                    physicalVenue.getVenueCapacity(),
                    physicalVenue.getAddress().getId(),
                    physicalVenue.getFloor(),
                    physicalVenue.getRoom()
            );

            BigInteger generatedId = databaseManager.executeUpdate(SQL_INSERT_CLUB, parameters, true);
            if (generatedId == null) {
                throw new SQLException("Creating physicalVenue failed, no ID obtained.");
            }

            physicalVenue.setId(generatedId);
            return physicalVenue;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to create physicalVenue: %s", e.getMessage()), e);
        }
    }

    public PhysicalVenue update(PhysicalVenue physicalVenue) {
        // TODO implementation
        return null;
    }

    public void delete(PhysicalVenue physicalVenue) {
        BigInteger physicalVenueId = physicalVenue.getId();
        String SQL_DELETE_CLUB = "DELETE FROM app.physical_venue WHERE id = (?);";

        try {
            List<Object> deleteParameters = List.of(physicalVenueId);
            databaseManager.executeUpdate(SQL_DELETE_CLUB, deleteParameters, false);

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to delete physicalVenue: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(PhysicalVenue physicalVenue) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.address WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(physicalVenue.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check venue existence: %s", e.getMessage()), e);
        }

        return output;
    }


    private PhysicalVenue mapVenue(ResultSet resultSet) throws SQLException {
        PhysicalVenue physicalVenue = new PhysicalVenue(BigInteger.valueOf(resultSet.getLong("id")));
        physicalVenue.setDescription(resultSet.getString("description"));
        physicalVenue.setCost(resultSet.getDouble("cost"));
        physicalVenue.setVenueCapacity(resultSet.getInt("venue_capacity"));
        physicalVenue.setFloor(resultSet.getString("floor"));
        physicalVenue.setRoom(resultSet.getString("room"));
        return physicalVenue;
    }
}

