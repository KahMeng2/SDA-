package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.dto.StudentFilterRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.util.List;

public class StudentService {
    private static StudentService instance;
    private final StudentMapper studentMapper;

    private StudentService() {
        this.studentMapper = StudentMapper.getInstance();
    }

    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    public List<Student> getAdminsForClub(BigInteger clubId) {
        if (!(new Club(clubId).exists())) {
            throw new VisibleException("Club with id " + clubId + " does not exist", HttpServletResponse.SC_NOT_FOUND);
        }

        return studentMapper.getAdminsForClub(new Club(clubId));
    }

    public void updateStudent(Student student) {
        // Validate the student object
        if (student.getId() == null) {
            throw new VisibleException("Student ID cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Call the mapper to update the student in the database
        studentMapper.update(student);
    }

    public void makeAdmin(BigInteger clubId, BigInteger existingAdminId, BigInteger newAdminId) {
        // Basic non-null validations
        if (clubId == null) {
            throw new VisibleException("clubId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (existingAdminId == null) {
            throw new VisibleException("existingAdminId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (newAdminId == null) {
            throw new VisibleException("newAdminId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        studentMapper.makeAdmin(new Club(clubId), new Student(existingAdminId), new Student(newAdminId));
    }

    public void revokeAdmin(BigInteger clubId, BigInteger existingAdminId, BigInteger revokedAdminId) {
        // Basic non-null validations
        if (clubId == null) {
            throw new VisibleException("clubId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (existingAdminId == null) {
            throw new VisibleException("existingAdminId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (revokedAdminId == null) {
            throw new VisibleException("revokedAdminId cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Call the mapper to revoke the admin role
        studentMapper.revokeAdmin(new Club(clubId), new Student(existingAdminId), new Student(revokedAdminId));
    }

    // Method to filter students based on the StudentFilterRequest parameters
    public List<Student> getFilteredStudents(StudentFilterRequest filterRequest) {
        // Validate the filter parameters
        if (filterRequest == null) {
            throw new VisibleException("Filter request cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // If no filters are applied, return an empty list or handle as needed
        if (filterRequest.getNameQuery() == null) {
            throw new VisibleException("At least one filter condition must be provided.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Validate individual filters as needed, e.g., nameQuery
        if (filterRequest.getNameQuery() != null && filterRequest.getNameQuery().isEmpty()) {
            throw new VisibleException("Name query cannot be an empty string.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Call the StudentMapper's method to apply filters and retrieve students
        List<Student> filteredStudents = studentMapper.getFilteredStudents(filterRequest);

        return filteredStudents;
    }
}