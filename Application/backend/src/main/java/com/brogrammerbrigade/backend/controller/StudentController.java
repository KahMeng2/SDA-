package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.Student;

import com.brogrammerbrigade.backend.dto.StudentFilterRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.service.ClubService;
import com.brogrammerbrigade.backend.service.UserService;
import com.brogrammerbrigade.backend.service.StudentService;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Student", urlPatterns = "/students/*")
public class StudentController extends HttpServlet {
    private final UserService userService;
    private final StudentService studentService;
    private final ClubService clubService;
    private final ObjectMapper objectMapper;
//    private final AuthenticationService authService;

    public StudentController() {
        this.userService = UserService.getInstance();
        this.studentService = StudentService.getInstance();
        this.clubService = ClubService.getInstance();
//        authService = new AuthenticationService(userService, TokenServiceFactory.createTokenService());
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo.equals("/makeAdmin")) {
            makeAdmin(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            if (pathInfo.equals("/adminsForClub")) {
                getAdminsForClub(req, resp);
            } else if (pathInfo.equals("/filter")) {
                getFilteredStudents(req, resp); }
            else {
                throw new VisibleException("Not Found", HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            throw new VisibleException("Not Found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            updateStudent(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }

        // User Story 9: Update event
        // Input: StudentID, eventID, Event Parameters.
        // auth student if is admin.

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            deleteStudent(req, resp);
        } else if (pathInfo.equals("/revokeAdmin")) {
            revokeAdmin(req, resp);
        } else {
            throw new VisibleException("Not Found", HttpServletResponse.SC_NOT_FOUND);
        }
    }


    private void getAdminsForClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String clubIdParam = req.getParameter("clubId");

        if (clubIdParam == null || clubIdParam.isEmpty()) {
            throw new VisibleException("Club ID is required.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger clubId = new BigInteger(clubIdParam);

        List<Student> admins = studentService.getAdminsForClub(clubId);

        JsonResponseUtil.sendJsonResponse(resp, admins, HttpServletResponse.SC_OK);
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Parse the incoming JSON request body into a Student object
        Student student = objectMapper.readValue(req.getInputStream(), Student.class);

        // Call the service to update the student
        studentService.updateStudent(student);

        // Send the updated student back in the response
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("status", "Updated student successfully");
        String jsonResponse = objectMapper.writeValueAsString(respMap);

        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
    }

    private void makeAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);

        // Extract parameters from JSON
        BigInteger existingAdminId = new BigInteger((String) jsonMap.get("existingAdminId"));
        BigInteger newAdminId = new BigInteger((String) jsonMap.get("newAdminId"));
        BigInteger clubId = new BigInteger((String) jsonMap.get("clubId"));

        studentService.makeAdmin(clubId, existingAdminId,  newAdminId);

        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void revokeAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);

        // Extract parameters from JSON
        BigInteger existingAdminId = new BigInteger((String) jsonMap.get("existingAdminId"));
        BigInteger revokedAdminId = new BigInteger((String) jsonMap.get("revokedAdminId"));
        BigInteger clubId = new BigInteger((String) jsonMap.get("clubId"));

        studentService.revokeAdmin(clubId, existingAdminId, revokedAdminId);

        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }


    private void deleteStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO
    }

    private void getFilteredStudents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Parse the incoming JSON request body into a StudentFilterRequest object
        StudentFilterRequest filterRequest = objectMapper.readValue(req.getInputStream(), StudentFilterRequest.class);

        // Use the studentService to filter students based on the filterRequest parameters
        List<Student> students = studentService.getFilteredStudents(filterRequest);

        JsonResponseUtil.sendJsonResponse(resp, students, HttpServletResponse.SC_OK);
    }


    private static class SignInRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}