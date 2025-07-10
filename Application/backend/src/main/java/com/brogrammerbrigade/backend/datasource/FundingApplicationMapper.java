package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationStatus;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class FundingApplicationMapper implements Mapper<FundingApplicationContext> {
    private static FundingApplicationMapper instance;
    private final DatabaseManager databaseManager;

    private FundingApplicationMapper() { this.databaseManager = DatabaseManager.getInstance(); }

    // Public method to provide access to the singleton instance
    public static synchronized FundingApplicationMapper getInstance() {
        if (instance == null) {
            instance = new FundingApplicationMapper();
        }
        return instance;
    }

    // Get all funding applications on the database
    public ArrayList<FundingApplicationContext> getAllFundingApplications() {
        String SQL_GET_ALL = "SELECT * FROM app.funding_application";
        try {
            ResultSet results = databaseManager.executeQuery(SQL_GET_ALL, List.of());
            ArrayList<FundingApplicationContext> fundingApps = new ArrayList<>();
            while (results.next()) {
                fundingApps.add(this.map(results));
            }
            return fundingApps;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Insert a funding application to the database
    public FundingApplicationContext insert(FundingApplicationContext application) {
        String SQL_INSERT = "INSERT INTO app.funding_application (club_id, description, amount, status, submitted_at, semester, year) VALUES (?,?,?,?::app.status_enum, NOW(), ?, ?);";
        try {
            List<Object> parameters = List.of(
                    application.getClubID(),
                    application.getDescription(),
                    application.getAmount(),
                    "In Draft", // This should match the enum value
                    application.getSemester(),
                    application.getYear().getValue()
            );
            // Get generated ID back from database
            BigInteger generatedId = databaseManager.executeUpdate(SQL_INSERT, parameters, true);
            if (generatedId == null) {
                throw new SQLException("Creating funding application failed, no ID returned.");
            }

            application.setId(generatedId);
            return application;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to create funding application: %s", e.getMessage()), e);
        }
    }

    // Check if an application exists for a given club and semester
    public boolean existsApplicationForClubAndSem(BigInteger clubID, Integer semester, Year year) {
        String SQL_CHECK = "SELECT EXISTS(SELECT 1 FROM app.funding_application WHERE club_id = ? AND semester = ? AND year = ?)";
        try {
            List<Object> parameters = List.of(clubID, semester, year.getValue());
            ResultSet resultSet = databaseManager.executeQuery(SQL_CHECK, parameters);
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existing applications", e);
        }
    }

    public FundingApplicationContext getFundingApplicationById(BigInteger id) {
        String SQL_GET_BY_ID = "SELECT * FROM app.funding_application WHERE id = ?";
        try {
            List<Object> parameters = List.of(id);
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_BY_ID, parameters);

            if (resultSet.next()) {
                return this.map(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve funding application by ID: %s", e.getMessage()), e);
        }
    }

    public List<FundingApplicationContext> getFundingApplicationsByClubId(BigInteger clubID) {
        String SQL_GET_BY_CLUB_ID = "SELECT * FROM app.funding_application WHERE club_id = ?";
        try {
            List<Object> parameters = List.of(clubID);
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_BY_CLUB_ID, parameters);
            List<FundingApplicationContext> applications = new ArrayList<>();
            while (resultSet.next()) {
                applications.add(this.map(resultSet));
            }
            return applications;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve funding applications for club ID %s: %s", clubID, e.getMessage()), e);
        }
    }

    public FundingApplicationContext update(FundingApplicationContext application) {
        String SQL_UPDATE = "UPDATE app.funding_application SET description = ?, amount = ?, status = ?::app.status_enum WHERE id = ?;";
        try {
            // Check if the funding application exists
            String SQL_CHECK_APP = "SELECT 1 FROM app.funding_application WHERE id = ?";
            boolean appExists = databaseManager.queryExists(SQL_CHECK_APP, List.of(application.getId()));
            if (!appExists) {
                throw new VisibleException("Funding application " + application.getId() + " does not exist.", HttpServletResponse.SC_NOT_FOUND);
            }

            // Prepare parameters for SQL update
            List<Object> parameters = List.of(
                    application.getDescription(),
                    application.getAmount(),
                    application.getStatusValue(),
                    application.getId()
            );

            databaseManager.executeUpdate(SQL_UPDATE, parameters, false);

            return application;

        } catch (VisibleException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to update funding application: %s", e.getMessage()), e);
        }
    }

    public void delete(FundingApplicationContext application) {
        BigInteger applicationId = application.getId();
        String SQL_DELETE = "DELETE FROM app.funding_application WHERE id = ?;";
        try {
            List<Object> parameters = List.of(applicationId);
            databaseManager.executeUpdate(SQL_DELETE, parameters, false);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to delete funding application: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(FundingApplicationContext application) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.funding_application WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(application.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check application existence: %s", e.getMessage()), e);
        }

        return output;
    }

    // Helper function to map database output to a FundingApplication object
    private FundingApplicationContext map(ResultSet resultSet) throws SQLException {
        // Prepare values for existing application constructor
        BigInteger id = BigInteger.valueOf(resultSet.getLong("id"));
        BigInteger clubId = BigInteger.valueOf(resultSet.getLong("club_id"));
        Integer semester = resultSet.getInt("semester");
        Year year = Year.of(resultSet.getInt("year"));
        String statusString = resultSet.getString("status");
        FundingApplicationStatus status = FundingApplicationStatus.valueOf(statusString.toUpperCase().replace(" ", "_"));

        // Create new application object
        FundingApplicationContext application = new FundingApplicationContext(id, clubId, semester, year, status);
        application.setDescription(resultSet.getString("description"));
        application.setAmount(resultSet.getDouble("amount"));
        application.setSubmittedAt(resultSet.getTimestamp("submitted_at") != null ?
                resultSet.getTimestamp("submitted_at").toLocalDateTime() : null);

        return application;
    }
}