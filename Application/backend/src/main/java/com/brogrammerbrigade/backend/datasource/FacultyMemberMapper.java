package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.domain.User;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// this is a mapper
public class FacultyMemberMapper implements UserMapper {
    private static FacultyMemberMapper instance;
    private final DatabaseManager databaseManager;

    private FacultyMemberMapper() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public static synchronized FacultyMemberMapper getInstance() {
        if (instance == null) {
            instance = new FacultyMemberMapper();
        }
        return instance;
    }

    public FacultyMember getUser(User user) {
        BigInteger id = user.getId();
        String SQL = "SELECT * FROM app.faculty_member WHERE id = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(id));
            if (resultSet.next()) {
                return mapResultSetToFacultyMember(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting faculty member by ID", e);
        }
        return null;
    }

    @Override
    public FacultyMember getUserByUsername(String username) {
        String SQL = "SELECT * FROM app.faculty_member WHERE username = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(username));
            if (resultSet.next()) {
                return mapResultSetToFacultyMember(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting faculty member by username", e);
        }
        return null;
    }

    public FacultyMember getFacultyMemberById(BigInteger id) {
        String SQL = "SELECT * FROM app.faculty_member WHERE id = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(id));
            if (resultSet.next()) {
                return mapResultSetToFacultyMember(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting faculty member by ID", e);
        }
        return null;
    }

    @Override
    public User insert(User user) {
        if (!(user instanceof FacultyMember facultyMember)) {
            throw new VisibleException("User must be a FacultyMember", HttpServletResponse.SC_BAD_REQUEST);
        }

        String SQL = "INSERT INTO app.faculty_member (email, username, password, first_name, middle_name, last_name, date_of_birth, department, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            List<Object> params = new ArrayList<>();
            params.add(facultyMember.getEmail());
            params.add(facultyMember.getUsername());
            params.add(facultyMember.getPassword());
            params.add(facultyMember.getFirstName());
            params.add(facultyMember.getMiddleName());
            params.add(facultyMember.getLastName());
            params.add(facultyMember.getDob() != null ? new Date(facultyMember.getDob().getTime()) : null);
            params.add(facultyMember.getDepartment());
            params.add(facultyMember.getRole());

            ResultSet rs = databaseManager.executeQuery(SQL, params);
            if (rs.next()) {
                facultyMember.setId(BigInteger.valueOf(rs.getLong(1)));
            } else {
                throw new SQLException("Creating faculty member failed, no ID obtained.");
            }

            return facultyMember;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating faculty member: " + e.getMessage(), e);
        }
    }

    @Override
    public User update(User user) {
        if (!(user instanceof FacultyMember facultyMember)) {
            throw new VisibleException("User must be a FacultyMember", HttpServletResponse.SC_BAD_REQUEST);
        }

        String SQL = "UPDATE app.faculty_member SET email = ?, username = ?, password = ?, first_name = ?, middle_name = ?, last_name = ?, date_of_birth = ?, department = ?, role = ? WHERE id = ?";
        try {
            List<Object> params = new ArrayList<>();
            params.add(facultyMember.getEmail());
            params.add(facultyMember.getUsername());
            params.add(facultyMember.getPassword());
            params.add(facultyMember.getFirstName());
            params.add(facultyMember.getMiddleName());
            params.add(facultyMember.getLastName());
            params.add(facultyMember.getDob() != null ? new Date(facultyMember.getDob().getTime()) : null);
            params.add(facultyMember.getDepartment());
            params.add(facultyMember.getRole());
            params.add(facultyMember.getId());

            databaseManager.executeUpdate(SQL, params, false);

            return facultyMember;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating faculty member: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(User facultyMember) {
        if (!(facultyMember instanceof FacultyMember)) {
            throw new VisibleException("User must be a FacultyMember", HttpServletResponse.SC_BAD_REQUEST);
        }

        String SQL = "DELETE FROM app.faculty_member WHERE id = ?";
        try {
            List<Object> params = new ArrayList<>();
            params.add(facultyMember.getId());

            databaseManager.executeUpdate(SQL, params, false);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting faculty member: " + e.getMessage(), e);
        }
    }

    public Boolean exists(User facultyMember) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.faculty_member WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(facultyMember.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check facultyMember existence: %s", e.getMessage()), e);
        }

        return output;
    }

    public boolean userExists(String username, String email) {
        String SQL = "SELECT COUNT(*) FROM app.faculty_member WHERE username = ? OR email = ?";
        try {
            ResultSet rs = DatabaseManager.getInstance().executeQuery(SQL, List.of(username, email));
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user exists: " + e.getMessage(), e);
        }
        return false;
    }

    private FacultyMember mapResultSetToFacultyMember(ResultSet rs) throws SQLException {
        FacultyMember facultyMember = new FacultyMember();
        facultyMember.setId(BigInteger.valueOf(rs.getLong("id")));
        facultyMember.setEmail(rs.getString("email"));
        facultyMember.setUsername(rs.getString("username"));
        facultyMember.setPassword(rs.getString("password"));
        facultyMember.setFirstName(rs.getString("first_name"));
        facultyMember.setMiddleName(rs.getString("middle_name"));
        facultyMember.setLastName(rs.getString("last_name"));
        facultyMember.setDob(rs.getDate("date_of_birth"));
        facultyMember.setDepartment(rs.getString("department"));
        return facultyMember;
    }
}