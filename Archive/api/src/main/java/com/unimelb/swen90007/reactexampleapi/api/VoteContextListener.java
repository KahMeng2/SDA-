package com.unimelb.swen90007.reactexampleapi.api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimelb.swen90007.reactexampleapi.domain.VoteService;
import com.unimelb.swen90007.reactexampleapi.port.postgres.ConnectionProvider;
import com.unimelb.swen90007.reactexampleapi.port.postgres.PostgresVoteRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.sql.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class VoteContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(VoteContextListener.class.getName());

    static final String VOTE_SERVICE = "voteService";
    static final String MAPPER = "mapper";
    private static final String PROPERTY_JDBC_URI = "jdbc.uri";
    private static final String PROPERTY_JDBC_USERNAME = "jdbc.username";
    private static final String PROPERTY_JDBC_PASSWORD = "jdbc.password";
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");
            LOGGER.info("Database URL: " + System.getProperty(PROPERTY_JDBC_URI));
            LOGGER.info("Database Username: " + System.getProperty(PROPERTY_JDBC_USERNAME));
            LOGGER.info("Database Password is set: " + (System.getProperty(PROPERTY_JDBC_PASSWORD) != null));
            String dbUrl = System.getProperty(PROPERTY_JDBC_URI);
            String dbUsername = System.getProperty(PROPERTY_JDBC_USERNAME);
            String dbPassword = System.getProperty(PROPERTY_JDBC_PASSWORD);

            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IllegalStateException("Database properties are not set correctly");
            }

            // Test database connection
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                LOGGER.info("Database connection successful");

            }

            var connectionProvider = new ConnectionProvider(dbUrl, dbUsername, dbPassword);
            connectionProvider.init();

            var voteRepository = new PostgresVoteRepository(connectionProvider);
            var voteService = new VoteService(voteRepository);
            sce.getServletContext().setAttribute(VOTE_SERVICE, voteService);

            ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                    .build();
            sce.getServletContext().setAttribute(MAPPER, mapper);

            LOGGER.info("Context initialized successfully");

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing context", e);
            throw new RuntimeException("Context initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Deregister JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                // Log the error
            }
        }

        ServletContextListener.super.contextDestroyed(sce);
    }
    private void logTablesInSchema(Connection connection, String schemaName) {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, schemaName);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Tables in schema '" + schemaName + "':");
                boolean tablesFound = false;

                while (rs.next()) {
                    System.out.println("- " + rs.getString("table_name"));
                    tablesFound = true;
                }

                if (!tablesFound) {
                    System.out.println("No tables found in schema '" + schemaName + "'");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error logging tables in schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}