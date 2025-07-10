package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.User;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name="auth", urlPatterns = {"/auth/*"})
public class AuthController extends HttpServlet {

    private final AuthenticationService authService = AuthenticationService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else if ("/signup".equals(pathInfo)) {
            handleSignup(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(req.getReader(), LoginRequest.class);
            User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String token = authService.generateToken(user);
            user.setPassword(null);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            JsonResponseUtil.sendJsonResponse(resp, response, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new VisibleException("Invalid username or password", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SignupRequest signupRequest = objectMapper.readValue(req.getReader(), SignupRequest.class);
        User user;
        String department = null;
        if ("STUDENT".equalsIgnoreCase(signupRequest.getUserType())) {
            user = new Student();
        } else if ("FACULTY".equalsIgnoreCase(signupRequest.getUserType())) {
            user = new FacultyMember();
            department = signupRequest.getDepartment();
            if (department == null || department.isEmpty()) {
                throw new VisibleException("Department is required for faculty members", HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            throw new VisibleException("Invalid user type", HttpServletResponse.SC_NOT_FOUND);
        }
        if (signupRequest.getEmail() == null || signupRequest.getUsername() == null || signupRequest.getPassword() == null ||
                signupRequest.getFirstName() == null || signupRequest.getLastName() == null) {
            throw new VisibleException("Email, username, password, first name, and last name are required", HttpServletResponse.SC_BAD_REQUEST);
        }
        user.setUsername(signupRequest.getUsername());
        user.setPassword(signupRequest.getPassword());
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setMiddleName(signupRequest.getMiddleName());
        user.setDob(signupRequest.getDob());
        // Set other fields as necessary
        System.out.println(signupRequest.getDepartment());
        User createdUser = authService.signUp(user, department);

       User authenticatedUser = authService.authenticate(createdUser.getUsername(), signupRequest.getPassword());
       String token = authService.generateToken(authenticatedUser);
        authenticatedUser.setPassword(null);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", authenticatedUser);

        JsonResponseUtil.sendJsonResponse(resp, response, HttpServletResponse.SC_CREATED);
    }

    private static class LoginRequest {
        private String username;
        private String password;

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

        // Getters and setters
    }

    private static class SignupRequest {
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        private String username;
        private String password;
        private String email;
        private String firstName;
        private String middleName;
        private String lastName;
        private Date dob;
        private String userType; // "STUDENT" or "FACULTY"
        private String department;

        public String getMiddleName() {
            return middleName;
        }
        public String getDepartment() {
            return department;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        // Getters and setters
    }
}