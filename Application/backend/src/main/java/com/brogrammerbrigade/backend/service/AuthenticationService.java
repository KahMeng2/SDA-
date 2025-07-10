package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.domain.User;
import com.brogrammerbrigade.backend.security.JwtTokenService;
import com.brogrammerbrigade.backend.security.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;

public class AuthenticationService {
    private static AuthenticationService instance;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final FundingApplicationService fundingApplicationService = FundingApplicationService.getInstance();
    private AuthenticationService() {
        this.userService = UserService.getInstance();
        this.jwtTokenService = JwtTokenService.getInstance();
    }

    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public User authenticate(String username, String password) {
        User user = userService.getUserByUsername(username);
        if (user == null || !PasswordEncoder.verify(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;

    }

    public String generateToken(User user) {
        String userType = user instanceof Student ? "STUDENT" : "FACULTY";
        String department = user instanceof FacultyMember ? ((FacultyMember) user).getDepartment() : null;
        return jwtTokenService.generateToken(user.getUsername(), user.getId(), user.getRole(), userType, user.getAuthorities(), department);
    }

    public User signUp(User user, String department) {
        if (userService.userExists(user.getUsername(), user.getEmail())) {
            throw new RuntimeException("Username or email already exists");
        }
        if (user instanceof FacultyMember && (department == null || department.isEmpty())) {
            throw new IllegalArgumentException("Department is required for faculty members");
        }
        return userService.createUser(user, department);
    }


    public boolean isAuthorized(String token, BigInteger userId) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            return false;
        }
        System.out.println(jwtTokenService.getUserIdFromToken(token));
        return jwtTokenService.getUserIdFromToken(token).equals(userId);
    }

//    public boolean hasPermission(String token, String requiredPermission) {
//        if (token == null || !jwtTokenService.validateToken(token)) {
//            return false;
//        }
//        String role = jwtTokenService.getRoleFromToken(token);
//        return checkPermission(role, requiredPermission, jwtTokenService.getUserIdFromToken(token));
//    }
    public boolean hasPermission(String token, String requiredPermission, BigInteger clubId) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            return false;
        }
        String role = jwtTokenService.getRoleFromToken(token);
        if (requiredPermission.equals("CLUB_ADMIN") && role.equals("STUDENT")){
            return isStudentClubAdmin(jwtTokenService.getUserIdFromToken(token), clubId);

        }

        return false;
//        return checkPermission(role, requiredPermission, jwtTokenService.getUserIdFromToken(token));
    }
    public boolean isEventAdmin(String token, String requiredPermission, BigInteger eventId) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            return false;
        }
        String role = jwtTokenService.getRoleFromToken(token);
        if (requiredPermission.equals("CLUB_ADMIN") && role.equals("STUDENT")){
            // Get the event's club ID
            EventService eventService = EventService.getInstance();
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                return false;
            }

            BigInteger clubId = event.getClubId();
            return isStudentClubAdmin(jwtTokenService.getUserIdFromToken(token), clubId);

        }
        return false;
    }
    public boolean isClubAdmin(String token, BigInteger clubId, boolean faculty) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            return false;
        }
        String role = jwtTokenService.getUserTypeFromToken(token);
        if (role.equals("FACULTY") && faculty) {
            return true;
        }
        if (role.equals("STUDENT")) {
            BigInteger userId = jwtTokenService.getUserIdFromToken(token);
            return isStudentClubAdmin(userId, clubId);
        }
        return false;
    }

    private boolean isStudentClubAdmin(BigInteger userId, BigInteger clubId) {

        return StudentMapper.getInstance().isStudentClubAdmin(userId, clubId);

    }

    public boolean isFundingApplicationAdmin(String token, BigInteger applicationId, boolean faculty) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            return false;
        }
        String role = jwtTokenService.getRoleFromToken(token);
        if (role.equals("FACULTY") && faculty) {
            return true;
        }
        if (role.equals("STUDENT")) {
            BigInteger userId = jwtTokenService.getUserIdFromToken(token);
            BigInteger clubId = fundingApplicationService.getClubIdForFundingApplication(applicationId);
            return isStudentClubAdmin(userId, clubId);
        }
        return false;
    }

    public BigInteger getIdFromToken(String token) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        return jwtTokenService.getUserIdFromToken(token);
    }

    public String getUserTypeFromToken(String token){
        if (token == null || !jwtTokenService.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        return jwtTokenService.getUserTypeFromToken(token);
    }
    public String getUsernameFromToken(String token) {
        if (token == null || !jwtTokenService.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        return jwtTokenService.getUsernameFromToken(token);
    }


}