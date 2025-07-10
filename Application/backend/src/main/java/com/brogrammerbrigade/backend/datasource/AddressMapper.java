package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.Address;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressMapper implements Mapper<Address> {
    private static AddressMapper instance;
    private final DatabaseManager databaseManager;
    private AddressMapper() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    // Public method to provide access to the singleton instance
    public static synchronized AddressMapper getInstance() {
        if (instance == null) {
            instance = new AddressMapper();
        }
        return instance;
    }

    public Address map(ResultSet resultSet) throws SQLException {
        Address address = new Address();
        address.setId(resultSet.getBigDecimal("id").toBigInteger());
        address.setAddressLine1(resultSet.getString("address_line_1"));
        // Sets addressLine 2 as empty string to stop infinite map loop
        address.setAddressLine2(resultSet.getString("address_line_2"));
        address.setCity(resultSet.getString("city"));
        address.setCountry(resultSet.getString("country"));
        address.setState(resultSet.getString("state"));
        address.setPostcode( resultSet.getInt("postcode"));
        return address;
    }

    public Address insert(Address address) {
        String SQL_INSERT_ADDRESS = "INSERT INTO app.address (address_line_1, address_line_2, city, state, country, postcode) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        try {
            // Create a list to hold parameters
            List<Object> parameters = new ArrayList<>();

            // Add parameters, explicitly handling potential null values
            parameters.add(address.getAddressLine1());
            parameters.add(address.getAddressLine2());
            parameters.add(address.getCity());
            parameters.add(address.getState());
            parameters.add(address.getCountry());
            parameters.add(address.getPostcode());

            BigInteger generatedId = DatabaseManager.getInstance().executeUpdate(SQL_INSERT_ADDRESS, parameters, true);
            if (generatedId == null) {
                throw new SQLException("Creating address failed, no ID obtained.");
            }

            // Set the generated BigInteger ID in the Address object
            address.setId(generatedId);
            return address;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to create address: %s", e.getMessage()), e);
        }
    }

    public Address update(Address address) {
        return null;
    }

    public void delete(Address address) {}

    public Boolean exists(Address address) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.address WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(address.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check address existence: %s", e.getMessage()), e);
        }

        return output;
    }

    public Address getAddressById(BigInteger addressId) {
        String SQL_GET_ADDRESS_BY_ID = "SELECT * FROM app.address WHERE id = ?";
        try {
            List<Object> parameters = List.of(addressId);
            ResultSet resultSet = DatabaseManager.getInstance().executeQuery(SQL_GET_ADDRESS_BY_ID, parameters);

            if (resultSet.next()) {
                return map(resultSet);
            } else {
                return null; // or throw a custom exception if address not found
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve address with ID %s: %s", addressId, e.getMessage()), e);
        }
    }

}
