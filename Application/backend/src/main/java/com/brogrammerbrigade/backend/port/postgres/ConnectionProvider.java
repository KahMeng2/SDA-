package com.brogrammerbrigade.backend.port.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ConnectionProvider {
    // CHange to private after fixes
    private static final String PROPERTY_JDBC_URI = "jdbc.uri";
    private static final String PROPERTY_JDBC_USERNAME = "jdbc.username";
    private static final String PROPERTY_JDBC_PASSWORD = "jdbc.password";
    private static final int MAX_CONNECTIONS = 50;
    private static final Duration ACQUIRE_CONNECTION_TIMEOUT = Duration.ofMillis(100);

    private final BlockingDeque<Connection> connectionPool;

    public ConnectionProvider() {
        this.connectionPool = new LinkedBlockingDeque<>();
    }

    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        while (connectionPool.size() < MAX_CONNECTIONS){
            connectionPool.offer(connect());
        }
    }

    public Connection nextConnection(){
        try{
            return connectionPool.poll(ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    public void releaseConnection(Connection connection) {
        try {
            connectionPool.offer(connection, ACQUIRE_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection(System.getProperty(PROPERTY_JDBC_URI), System.getProperty(PROPERTY_JDBC_USERNAME), System.getProperty(PROPERTY_JDBC_PASSWORD));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
