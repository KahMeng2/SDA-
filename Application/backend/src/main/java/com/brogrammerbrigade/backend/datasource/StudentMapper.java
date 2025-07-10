package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.domain.User;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;
import com.brogrammerbrigade.backend.dto.StudentFilterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentMapper implements UserMapper {

    private static StudentMapper instance;
    private final DatabaseManager databaseManager;

    private StudentMapper() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public static synchronized StudentMapper getInstance() {
        if (instance == null) {
            instance = new StudentMapper();
        }
        return instance;
    }

    public Student getUser(User user) {
        BigInteger id = user.getId();
        String SQL = "SELECT * FROM app.student WHERE id = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(id));
            if (resultSet.next()) {
                Student student = map(resultSet);
                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting student by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public Student getUserByUsername(String username) {
        String SQL = "SELECT s.*, c.id as club_id, c.name as club_name, c.balance as club_balance " +
                "FROM app.student s " +
                "LEFT JOIN app.admin_role ar ON s.id = ar.student_id " +
                "LEFT JOIN app.club c ON ar.club_id = c.id " +
                "WHERE s.username = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(username));
            Student student = null;
            List<Club> administeredClubs = new ArrayList<>();

            while (resultSet.next()) {
                if (student == null) {
                    student = map(resultSet);
                }

                BigInteger clubId = resultSet.getObject("club_id", BigInteger.class);
                if (clubId != null) {
                    Club club = new Club(clubId);
                    club.setId(clubId);
                    club.setName(resultSet.getString("club_name"));
                    club.setBalance(resultSet.getDouble("club_balance"));
                    administeredClubs.add(club);
                }
            }

            if (student != null) {
                student.setAdministratedClubs(administeredClubs);
            }

            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting student by username: " + e.getMessage(), e);
        }
    }

    public User insert(User user) {
        if (!(user instanceof Student student)) {
            throw new VisibleException("User must be a Student", HttpServletResponse.SC_BAD_REQUEST);
        }
        String SQL = "INSERT INTO app.student (email, username, password, first_name, middle_name, last_name, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            List<Object> params = new ArrayList<>();
            params.add(student.getEmail());
            params.add(student.getUsername());
            params.add(student.getPassword());
            params.add(student.getFirstName());
            params.add(student.getMiddleName() != null ? student.getMiddleName() : "");
            params.add(student.getLastName());
            params.add(student.getDob() != null ? new Date(student.getDob().getTime()) : null);

            BigInteger generatedId = databaseManager.executeUpdate(SQL, params, true);

            if (generatedId != null) {
                student.setId(generatedId);
            } else {
                throw new SQLException("Creating student failed, no ID obtained.");
            }

            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating student: " + e.getMessage(), e);
        }
    }

    public User update(User user) {
        if (!(user instanceof Student student)) {
            throw new VisibleException("User must be a Student", HttpServletResponse.SC_FORBIDDEN);
        }

        // Start building the SQL query
        StringBuilder sqlBuilder = new StringBuilder("UPDATE app.student SET ");
        List<Object> params = new ArrayList<>();

        // Dynamically add fields to update based on non-null values
        if (student.getEmail() != null) {
            sqlBuilder.append("email = ?, ");
            params.add(student.getEmail());
        }
        if (student.getUsername() != null) {
            sqlBuilder.append("username = ?, ");
            params.add(student.getUsername());
        }
        if (student.getPassword() != null) {
            sqlBuilder.append("password = ?, ");
            params.add(student.getPassword());
        }
        if (student.getFirstName() != null) {
            sqlBuilder.append("first_name = ?, ");
            params.add(student.getFirstName());
        }
        if (student.getMiddleName() != null) {
            sqlBuilder.append("middle_name = ?, ");
            params.add(student.getMiddleName());
        } else {
            sqlBuilder.append("middle_name = '', ");  // Default to empty string if middle name is not provided
        }
        if (student.getLastName() != null) {
            sqlBuilder.append("last_name = ?, ");
            params.add(student.getLastName());
        }
        if (student.getDob() != null) {
            sqlBuilder.append("date_of_birth = ?, ");
            params.add(new Date(student.getDob().getTime()));
        }

        // Remove trailing comma and add WHERE clause
        sqlBuilder.setLength(sqlBuilder.length() - 2);
        sqlBuilder.append(" WHERE id = ?");

        // Add the student ID to the parameters
        params.add(student.getId());

        // Execute the SQL update
        String SQL = sqlBuilder.toString();

        try {
            databaseManager.executeUpdate(SQL, params, false);
            return student;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating student: " + e.getMessage(), e);
        }
    }

    public void delete(User student) {
        BigInteger studentId = student.getId();
        String SQL = "DELETE FROM app.student WHERE id = ?";
        try {
            databaseManager.executeUpdate(SQL, List.of(studentId), false);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student: " + e.getMessage(), e);
        }
    }

    public Boolean exists(User student) {
        String SQL_CHECK_EXISTS = "SELECT 1 FROM app.student WHERE id = ?;";
        Boolean output = null;

        try {
            output = databaseManager.queryExists(SQL_CHECK_EXISTS, List.of(student.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to check student existence: %s", e.getMessage()), e);
        }

        return output;
    }

    public List<Student> getAdminsForClub(Club club) {
        BigInteger clubId = club.getId();
        String SQL_GET_FOR_CLUB = "SELECT s.* FROM app.student s " +
                "JOIN app.admin_role ar ON s.id = ar.student_id " +
                "WHERE ar.club_id = ?";

        try {
            List<Object> parameters = List.of(clubId);
            ResultSet results = databaseManager.executeQuery(SQL_GET_FOR_CLUB, parameters);
            ArrayList<Student> students = new ArrayList<>();
            while (results.next()) {
                students.add(this.map(results));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeAdmin(Club club, Student existingAdmin, Student newAdmin) {
        BigInteger clubId = club.getId();
        BigInteger existingAdminId = existingAdmin.getId();
        BigInteger newAdminId = newAdmin.getId();

        String SQL_CHECK_EXISTING_ADMIN = "SELECT COUNT(*) FROM app.admin_role WHERE club_id = ? AND student_id = ?;";
        String SQL_CHECK_NEW_ADMIN = "SELECT COUNT(*) FROM app.admin_role WHERE club_id = ? AND student_id = ?;";
        String SQL_MAKE_ADMIN = "INSERT INTO app.admin_role (club_id, student_id) VALUES (?, ?);";

        try {
            // Check if the existingAdminId is an admin for the club
            ResultSet resultSet = databaseManager.executeQuery(SQL_CHECK_EXISTING_ADMIN, List.of(clubId, existingAdminId));
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Check if the newAdminId is already an admin for the club
                resultSet = databaseManager.executeQuery(SQL_CHECK_NEW_ADMIN, List.of(clubId, newAdminId));
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    // If newAdminId is not already an admin, proceed to add them as an admin
                    databaseManager.executeUpdate(SQL_MAKE_ADMIN, List.of(clubId, newAdminId), false);
                } else {
                    throw new VisibleException("The newAdminId is already an admin for the club.", HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                throw new VisibleException("The specified existingAdminId is not an admin for the club.", HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error processing admin update: " + e.getMessage(), e);
        }
    }



    public void revokeAdmin(Club club, Student existingAdmin, Student revokedAdmin) {
        BigInteger clubId = club.getId();
        BigInteger existingAdminId = existingAdmin.getId();
        BigInteger revokedAdminId = revokedAdmin.getId();

        String SQL_CHECK_EXISTING_ADMIN = "SELECT COUNT(*) FROM app.admin_role WHERE club_id = ? AND student_id = ?;";
        String SQL_CHECK_REVOKED_ADMIN = "SELECT COUNT(*) FROM app.admin_role WHERE club_id = ? AND student_id = ?;";
        String SQL_REVOKE_ADMIN = "DELETE FROM app.admin_role WHERE club_id = ? AND student_id = ?;";

        try {
            // Check if the existingAdminId is an admin for the club
            ResultSet resultSet = databaseManager.executeQuery(SQL_CHECK_EXISTING_ADMIN, List.of(clubId, existingAdminId));

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Check if the revokedAdminId is actually an admin for the club
                resultSet = databaseManager.executeQuery(SQL_CHECK_REVOKED_ADMIN, List.of(clubId, revokedAdminId));
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If revokedAdminId is an admin, proceed to revoke the admin role
                    databaseManager.executeUpdate(SQL_REVOKE_ADMIN, List.of(clubId, revokedAdminId), false);
                } else {
                    throw new VisibleException("The specified revokedAdminId is not an admin for the club.", HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                throw new VisibleException("The specified existingAdminId is not an admin for the club.", HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error processing admin revocation: " + e.getMessage(), e);
        }
    }


    public ArrayList<Student> getFilteredStudents(StudentFilterRequest filterRequest) {
        // Base SQL query
        StringBuilder query = new StringBuilder("SELECT * FROM app.student WHERE 1=1");
        ArrayList<Object> parameters = new ArrayList<>();

        // Dynamically build the query based on the filters provided
        if (filterRequest.getNameQuery() != null && !filterRequest.getNameQuery().isEmpty()) {
            query.append(" AND name ILIKE ?");
            parameters.add("%" + filterRequest.getNameQuery() + "%");
        }

        // Execute the query
        try {
            ResultSet results = databaseManager.executeQuery(query.toString(), parameters);
            ArrayList<Student> students = new ArrayList<>();

            // Map the result set to Student objects
            while (results.next()) {
                students.add(this.map(results));
            }

            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered students: " + e.getMessage(), e);
        }
    }


    @Override
    public boolean userExists(String username, String email) {
        String SQL = "SELECT 1 FROM app.student WHERE username = ? OR email = ?";
        try {
            System.out.println(username + " " + email);
            return databaseManager.queryExists(SQL, List.of(username, email));
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if student exists: " + e.getMessage(), e);
        }
    }
    public boolean isStudentClubAdmin(BigInteger studentId, BigInteger clubId) {
        String SQL = "SELECT COUNT(*) FROM app.admin_role WHERE student_id = ? AND club_id = ?";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL, List.of(studentId, clubId));
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if student is club admin", e);
        }
        return false;
    }

    private Student map(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(BigInteger.valueOf(rs.getLong("id")));
        student.setEmail(rs.getString("email"));
        student.setUsername(rs.getString("username"));
        student.setPassword(rs.getString("password"));
        student.setFirstName(rs.getString("first_name"));
        student.setMiddleName(rs.getString("middle_name"));
        student.setLastName(rs.getString("last_name"));
        student.setDob(rs.getDate("date_of_birth"));
        return student;
    }
}