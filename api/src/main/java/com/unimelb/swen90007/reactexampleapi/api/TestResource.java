package com.unimelb.swen90007.reactexampleapi.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "test", urlPatterns = "/test")
public class TestResource extends HttpServlet {
    private static final String PROPERTY_JDBC_URI = "jdbc.uri";
    private static final String PROPERTY_JDBC_USERNAME = "jdbc.username";
    private static final String PROPERTY_JDBC_PASSWORD = "jdbc.password";
    private static final String SQL_GET_TEST = "SELECT * FROM app.test;";
    private static final String SQL_INSERT_TEST = "INSERT INTO app.test (test_value) VALUES (?);";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // open a connection and fetch the rows from the 'test' table and return
        try (Connection connection = DriverManager.getConnection(
                System.getProperty(PROPERTY_JDBC_URI),
                System.getProperty(PROPERTY_JDBC_USERNAME),
                System.getProperty(PROPERTY_JDBC_PASSWORD)))
        {
            try (PreparedStatement statement = connection.prepareStatement(SQL_GET_TEST)) {
                ResultSet results = statement.executeQuery();
                boolean dataFound = false;
                while (results.next()) {
                    // Print each row's 'test_value' field to the response
                    resp.getWriter().println(results.getString("test_value"));
                    dataFound = true;
                }
                if (!dataFound) {
                    resp.getWriter().println("No data found.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read the test_value parameter from the request
        String testValue = req.getParameter("test_value");

        if (testValue == null || testValue.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing or empty 'test_value' parameter.");
            return;
        }

        // Insert the new value into the 'test' table
        try (Connection connection = DriverManager.getConnection(
                System.getProperty(PROPERTY_JDBC_URI),
                System.getProperty(PROPERTY_JDBC_USERNAME),
                System.getProperty(PROPERTY_JDBC_PASSWORD)))
        {
            try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_TEST)) {
                statement.setString(1, testValue);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().println("Data inserted successfully.");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println("Failed to insert data.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        // load the driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        super.init();
    }
}