package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class ClubMapper implements Mapper<Club> {
    private static ClubMapper instance;
    private final DatabaseManager databaseManager;

    private ClubMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    // Public method to provide access to the singleton instance
    public static synchronized ClubMapper getInstance() {
        if (instance == null) {
            instance = new ClubMapper();
        }
        return instance;
    }

    public ArrayList<Club> getAllClubs() {
        String SQL_GET_ALL = "SELECT * FROM app.club";
        try {
            ResultSet results = databaseManager.executeQuery(SQL_GET_ALL, List.of());
            ArrayList<Club> clubs = new ArrayList<>();
            while (results.next()) {
                clubs.add(this.map(results));
            }
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Club getClubById(BigInteger id) {
        String SQL_GET_BY_ID = "SELECT * FROM app.club WHERE id = ?";

        try {
            List<Object> parameters = List.of(id); // Pass the club id as a parameter
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_BY_ID, parameters);

            // If a result is found, map it to a Club object
            if (resultSet.next()) {
                return this.map(resultSet);
            } else {
                return null;            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve club by ID: %s", e.getMessage()), e);
        }
    }

    public List<Club> getAdminClubsForStudent(Student student) {
        if (!StudentMapper.getInstance().exists(student)) {
            throw new VisibleException("Student not found with ID: " + student.getId(), HttpServletResponse.SC_NOT_FOUND);
        }


        BigInteger studentId = student.getId();
        System.out.println("Student ID: " + studentId);
        String SQL = "SELECT c.* FROM app.club c JOIN app.admin_role ar ON c.id = ar.club_id WHERE ar.student_id = ?";
        List<Club> clubs = new ArrayList<>();
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(studentId));
            while (resultSet.next()) {
                clubs.add(this.map(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting administered clubs: " + e.getMessage(), e);
        }
        return clubs;
    }

    public Club insert(Club club) {
        String SQL_INSERT_CLUB = "INSERT INTO app.club (name, balance) VALUES (?,?);";
        List<Object> parameters = new ArrayList<>();

        parameters.add(club.getName());
        parameters.add(club.getBalance());

        BigInteger generatedId = null;
        try {
            generatedId = databaseManager.executeUpdate(SQL_INSERT_CLUB, parameters, true);

            if (generatedId == null) {
                throw new RuntimeException("Failed to create club");
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // Unique constraint violation
                throw new VisibleException("A club with this name already exists. Please choose another name.", HttpServletResponse.SC_CONFLICT);
            }
            throw new RuntimeException(String.format("Failed to create club: %s", e.getMessage()), e);
        }

        club.setId(generatedId);
        return club;
    }

    public Club update(Club club) {
        if (!exists(club)) {
            throw new VisibleException("Club not found with ID: " + club.getId(), HttpServletResponse.SC_NOT_FOUND);
        }

        StringBuilder SQL_UPDATE_CLUB = new StringBuilder("UPDATE app.club SET name = ?, balance = ? WHERE id = ?;");
        List<Object> parameters = new ArrayList<>();

        parameters.add(club.getName());
        parameters.add(club.getBalance());
        parameters.add(club.getId());

        try {
            databaseManager.executeUpdate(SQL_UPDATE_CLUB.toString(), parameters, false);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // Unique constraint violation
                throw new VisibleException("A club with this name already exists. Please choose another name.", HttpServletResponse.SC_CONFLICT);
            }
            throw new RuntimeException(String.format("Failed to update club: %s", e.getMessage()), e);
        }

        return club;
    }


    public void delete(Club club) {
        if (!exists(club)) {
            throw new VisibleException("Club not found with ID: " + club.getId(), HttpServletResponse.SC_NOT_FOUND);
        }

        String SQL_DELETE_CLUB = "DELETE FROM app.club WHERE id = (?);";
        BigInteger clubId = club.getId();

        try {
            List<Object> deleteParameters = List.of(clubId);
            databaseManager.executeUpdate(SQL_DELETE_CLUB, deleteParameters, false);

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to delete club: %s", e.getMessage()), e);
        }
    }



    public void addAdmin(Club club, Student admin) {
        if (!exists(club)) {
            throw new VisibleException("Club not found with ID: " + club.getId(), HttpServletResponse.SC_NOT_FOUND);
        }

        if (!StudentMapper.getInstance().exists(admin)) {
            throw new VisibleException("Student not found with ID: " + admin.getId(), HttpServletResponse.SC_NOT_FOUND);
        }

        String SQL_INSERT_ADMIN_ROLE = "INSERT INTO app.admin_role (club_id, student_id) VALUES (?,?);";

        try {
            List<Object> parameters = List.of(club.getId(), admin.getId());
            databaseManager.executeUpdate(SQL_INSERT_ADMIN_ROLE, parameters, false);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to add admin: %s", e.getMessage()), e);
        }
    }

    public Boolean exists(Club club) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.club WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(club.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check club existence: %s", e.getMessage()), e);
        }

        return output;
    }


    // Helper function to map ResultSet to Club object
    private Club map(ResultSet resultSet) throws SQLException {
        Club club = new Club();
        club.setId(BigInteger.valueOf(resultSet.getLong("id")));
        club.setName(resultSet.getString("name"));
        club.setBalance(resultSet.getDouble("balance"));
        return club;
    }
}
