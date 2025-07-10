package com.brogrammerbrigade.backend.port.postgres;

import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final ConnectionProvider connectionProvider;

    // Private constructor to prevent direct instantiation
    private DatabaseManager() {
        this.connectionProvider = new ConnectionProvider();
        this.connectionProvider.init(); // Initializes the connection pool
    }

    // Public method to provide access to the singleton instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Executes a SELECT query and returns a ResultSet for the mapper to process.
     * @param sql The SQL SELECT query.
     * @param parameters List of query parameters.
     * @return The ResultSet of the query.
     * @throws SQLException If there's an issue executing the query.
     */
    public ResultSet executeQuery(String sql, List<Object> parameters) throws SQLException {
        Connection connection = connectionProvider.nextConnection();
        System.out.println("Executing query: " + sql);
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            setParameters(statement, parameters);
            return statement.executeQuery();
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE statement.
     * If the statement is an INSERT and a key is generated, it returns the generated key as BigInteger.
     * Otherwise, it returns null.
     *
     * @param sql        The SQL statement to execute.
     * @param parameters List of query parameters.
     * @param returnKey  Whether to return the generated key (for INSERT statements).
     * @return The generated key as BigInteger (for INSERT statements) or null.
     * @throws SQLException If there's an issue executing the query.
     */
    public BigInteger executeUpdate(String sql, List<Object> parameters, boolean returnKey) throws SQLException {
        Connection connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    returnKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS
            );
            setParameters(statement, parameters);

            int affectedRows = statement.executeUpdate();
            System.out.println(sql);
            if (affectedRows == 0) {
                throw new SQLException("No rows affected.");
            }


            if (returnKey) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return BigInteger.valueOf(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Insert failed, no ID obtained.");
                    }
                }
            }
            return null;
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    /**
     * Executes a batch of INSERT, UPDATE, or DELETE statements in a single transaction.
     * @param sqlBatch List of SQL statements to execute.
     * @param parametersBatch List of parameter lists for each SQL statement.
     * @return List of generated keys (for INSERT statements) or null.
     * @throws SQLException If there's an issue executing the batch.
     */
    public List<BigInteger> executeBatchUpdate(List<String> sqlBatch, List<List<Object>> parametersBatch) throws SQLException {

        Connection connection = connectionProvider.nextConnection();
        List<BigInteger> generatedKeys = new ArrayList<>();

        try {

            for (int i = 0; i < sqlBatch.size(); i++) {
                String sql = sqlBatch.get(i);
                List<Object> parameters = parametersBatch.get(i);

                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                setParameters(statement, parameters);

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Statement execution failed: " + sql);
                }

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedKeys.add(BigInteger.valueOf(keys.getLong(1)));
                    }
                }
            }

            connection.commit();
            return generatedKeys;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            connectionProvider.releaseConnection(connection);
        }
    }

    /**
     * Executes a SELECT query and returns true if any rows are found, false otherwise.
     * @param sql The SQL SELECT query.
     * @param parameters List of query parameters.
     * @return True if a result is found, false otherwise.
     * @throws SQLException If there's an issue executing the query.
     */
    public boolean queryExists(String sql, List<Object> parameters) throws SQLException {
        Connection connection = connectionProvider.nextConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                var res = rs.next();

                System.out.println(res);
                return res;  // Returns true if at least one result exists
            }
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    /**
     * Sets the parameters for a PreparedStatement.
     * @param statement The PreparedStatement object.
     * @param parameters The list of parameters to set.
     * @throws SQLException If there's an issue setting the parameters.
     */
    private void setParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i)); // PreparedStatement index starts at 1
        }
    }
}
