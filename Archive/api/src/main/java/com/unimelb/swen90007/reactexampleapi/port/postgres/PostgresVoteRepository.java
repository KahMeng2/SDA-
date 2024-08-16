package com.unimelb.swen90007.reactexampleapi.port.postgres;


import com.unimelb.swen90007.reactexampleapi.domain.Vote;
import com.unimelb.swen90007.reactexampleapi.domain.VoteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PostgresVoteRepository implements VoteRepository {

    private final ConnectionProvider connectionProvider;

    public PostgresVoteRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Vote save(Vote vote) {
        Connection connection = connectionProvider.nextConnection();
        try {
            if (vote.isNew()) {
                return insert(vote, connection);
            } else {
                return update(vote, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    private Vote insert(Vote vote, Connection connection) throws SQLException {
        String sql = "INSERT INTO app.vote (id, name, email, supporting, status, created) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            vote.setId(UUID.randomUUID().toString()); // Generate new ID for insert
            statement.setObject(1, UUID.fromString(vote.getId()));
            statement.setString(2, vote.getName());
            statement.setString(3, vote.getEmail());
            statement.setBoolean(4, vote.isSupporting());
            statement.setString(5, vote.getStatus().name());
            statement.setObject(6, vote.getCreated());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating vote failed, no rows affected.");
            }
            vote.setNew(false);
            return vote;
        }
    }

    private Vote update(Vote vote, Connection connection) throws SQLException {
        String sql = "UPDATE app.vote SET name = ?, email = ?, supporting = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vote.getName());
            statement.setString(2, vote.getEmail());
            statement.setBoolean(3, vote.isSupporting());
            statement.setString(4, vote.getStatus().name());
            statement.setObject(5, UUID.fromString(vote.getId()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating vote failed, no rows affected.");
            }
            return vote;
        }
    }

    @Override
    public Optional<Vote> findById(String id) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.vote WHERE id = ?"
            );
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToVote(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find vote by id", e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public Optional<Vote> get(String id) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.vote WHERE id = ?"
            );
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(map(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to get vote: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public List<Vote> getValid() {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.vote"
            );
            ResultSet resultSet = statement.executeQuery();
            System.out.println(resultSet.getMetaData().getColumnCount());

            List<Vote> votes = new ArrayList<>();
            while (resultSet.next()) {
                votes.add(map(resultSet));
            }
            return votes;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to get valid votes: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public List<Vote> getAll(long offset, long limit) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.vote ORDER BY created DESC OFFSET ? LIMIT ?"
            );
            statement.setLong(1, offset);
            statement.setLong(2, limit);
            ResultSet resultSet = statement.executeQuery();
            List<Vote> votes = new ArrayList<>();
            while (resultSet.next()) {
                votes.add(map(resultSet));
            }
            return votes;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to get all votes: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    private Vote map(ResultSet resultSet) throws SQLException {
        var vote = new Vote();
        vote.setNew(false);
        vote.setId(resultSet.getString("id"));
        vote.setName(resultSet.getString("name"));
        vote.setEmail(resultSet.getString("email"));
        vote.setSupporting(resultSet.getBoolean("supporting"));
        vote.setStatus(Vote.Status.valueOf(resultSet.getString("status")));
        vote.setCreated(resultSet.getObject("created", OffsetDateTime.class));
        return vote;
    }
    private Vote mapResultSetToVote(ResultSet rs) throws SQLException {
        Vote vote = new Vote();
        vote.setId(rs.getString("id"));
        vote.setName(rs.getString("name"));
        vote.setEmail(rs.getString("email"));
        vote.setSupporting(rs.getBoolean("supporting"));
        vote.setStatus(Vote.Status.valueOf(rs.getString("status")));
        vote.setCreated(rs.getObject("created", OffsetDateTime.class));
        vote.setNew(false); // Since this is coming from the database, it's not a new vote
        return vote;
    }
}