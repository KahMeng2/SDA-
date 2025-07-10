package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.dto.ClubRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.ClubService;
import com.brogrammerbrigade.backend.service.UserService;
import com.brogrammerbrigade.backend.util.AuthUtil;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

@WebServlet(name = "Clubs", urlPatterns = {"/clubs/*"})
public class ClubController extends HttpServlet {
    private ClubService clubService;
    private ObjectMapper objectMapper;
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        clubService = ClubService.getInstance();

        // Initialize AuthenticationServicepackage com.brogrammerbrigade.backend.controller;
        //
        //import com.brogrammerbrigade.backend.dto.ClubRequest;
        //import com.brogrammerbrigade.backend.exception.VisibleException;
        //import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
        //import com.brogrammerbrigade.backend.domain.Club;
        //import com.brogrammerbrigade.backend.service.AuthenticationService;
        //import com.brogrammerbrigade.backend.service.ClubService;
        //import com.brogrammerbrigade.backend.service.UserService;
        //import com.brogrammerbrigade.backend.util.AuthUtil;
        //import com.brogrammerbrigade.backend.util.JsonResponseUtil;
        //import jakarta.servlet.ServletException;
        //import jakarta.servlet.annotation.WebServlet;
        //import jakarta.servlet.http.HttpServlet;
        //import jakarta.servlet.http.HttpServletRequest;
        //import jakarta.servlet.http.HttpServletResponse;
        //import com.fasterxml.jackson.databind.ObjectMapper;
        //import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
        //import com.fasterxml.jackson.databind.SerializationFeature;
        //
        //import java.io.BufferedReader;
        //import java.io.IOException;
        //import java.util.HashMap;
        //import java.util.List;
        //import java.util.Map;
        //import java.math.BigInteger;
        //
        //@WebServlet(name = "Clubs", urlPatterns = {"/clubs/*"})
        //public class ClubController extends HttpServlet {
        //    private ClubService clubService;
        //    private ObjectMapper objectMapper;
        //    private AuthenticationService authService;
        //
        //    @Override
        //    public void init() throws ServletException {
        //        ConnectionProvider connectionProvider = new ConnectionProvider();
        //        connectionProvider.init();
        //        clubService = ClubService.getInstance();
        //
        //        // Initialize AuthenticationService
        //        UserService userService = UserService.getInstance();
        //        authService = AuthenticationService.getInstance();
        //
        //        objectMapper = new ObjectMapper();
        //        objectMapper.registerModule(new JavaTimeModule());
        //        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //
        //        super.init();
        //    }
        //
        //    @Override
        //    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //        String pathInfo = req.getPathInfo();
        //        if (pathInfo == null || pathInfo.equals("/")) {
        //            getAllClubs(req, resp);
        //        } else if (pathInfo.matches("/\\d+")) {
        //            BigInteger clubId = new BigInteger(pathInfo.substring(1));
        //            getClubById(req, resp, clubId);
        //        } else if (pathInfo.equals("/forAdmin")) {
        //            getAdminClubsForStudent(req, resp);
        //        }else {
        //            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        //        }
        //    }
        //
        //    @Override
        //    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //        String pathInfo = req.getPathInfo();
        //        if (pathInfo == null || pathInfo.equals("/")) {
        //            updateClub(req, resp);
        //        } else {
        //            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        //        }
        //    }
        //
        //    @Override
        //    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //        String pathInfo = req.getPathInfo();
        //        if (pathInfo == null || pathInfo.equals("/")) {
        //            createClub(req, resp);
        //        } else {
        //            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        //        }
        //
        //    }
        //
        //    @Override
        //    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //        String pathInfo = req.getPathInfo();
        //
        //        if (pathInfo == null || pathInfo.equals("/")) {
        //            deleteClub(req, resp);
        //        } else {
        //            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        //        }
        //    }
        //
        //    private void getAllClubs(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //        List<Club> clubs = clubService.getAllClubs();
        //        JsonResponseUtil.sendJsonResponse(resp, clubs, HttpServletResponse.SC_OK);
        //    }
        //
        //    private void getClubById(HttpServletRequest req, HttpServletResponse resp, BigInteger clubId) throws IOException {
        //        Club club = clubService.getClubById(clubId);
        //        if (club == null) {
        //            throw new VisibleException("Club not found", HttpServletResponse.SC_NOT_FOUND);
        //        } else {
        //            JsonResponseUtil.sendJsonResponse(resp, club, HttpServletResponse.SC_OK);
        //        }
        //    }
        //
        //    private void getAdminClubsForStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //        String studentIdParam = req.getParameter("studentId");
        //
        //        if (studentIdParam == null || studentIdParam.isEmpty()) {
        //            throw new VisibleException("Club ID is required", HttpServletResponse.SC_BAD_REQUEST);
        //        }
        //
        //        BigInteger studentId = new BigInteger(studentIdParam);
        //        List<Club> clubs = clubService.getAdminClubsForStudent(studentId);
        //        JsonResponseUtil.sendJsonResponse(resp, clubs, HttpServletResponse.SC_OK);
        //    }
        //
        //    private void deleteClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //        BufferedReader reader = req.getReader();
        //        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        //        BigInteger clubId = new BigInteger((String) jsonMap.get("clubId"));
        //
        //        if (clubService.getClubById(clubId) == null) {
        //            throw new VisibleException("Club with id " + clubId + " not found", HttpServletResponse.SC_NOT_FOUND);
        //        }
        //
        //        String token = AuthUtil.extractToken(req);
        //        if (!authService.hasPermission(token, "CLUB_ADMIN", clubId)) {
        //            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        //        }
        //
        //        clubService.deleteClub(clubId);
        //        Map<String, Object> respMap = new HashMap<>();
        //        respMap.put("status", "Club successfully deleted");
        //        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
        //    }
        //    private void createClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //        BufferedReader reader = req.getReader();
        //        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        //        // Extract parameters from JSON
        //        String name = (String) jsonMap.get("name");
        //        BigInteger adminId = new BigInteger((String) jsonMap.get("adminId"));
        //        Double balance = new Double((String) jsonMap.get("balance"));
        //        // Call the ClubService to create the club
        //        Club createdClub = clubService.createClub(name, balance, adminId);
        //
        //        // Build response
        //        Map<String, Object> respMap = new HashMap<>();
        //        respMap.put("clubId", createdClub.getId());      // Include club ID
        //        respMap.put("clubName", createdClub.getName());  // Include club name
        UserService userService = UserService.getInstance();
        authService = AuthenticationService.getInstance();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllClubs(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            BigInteger clubId = new BigInteger(pathInfo.substring(1));
            getClubById(req, resp, clubId);
        } else if (pathInfo.equals("/forAdmin")) {
            getAdminClubsForStudent(req, resp);
        }else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            updateClub(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            createClub(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            deleteClub(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getAllClubs(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        List<Club> clubs = clubService.getAllClubs();
        JsonResponseUtil.sendJsonResponse(resp, clubs, HttpServletResponse.SC_OK);
    }

    private void getClubById(HttpServletRequest req, HttpServletResponse resp, BigInteger clubId) throws IOException {
        Club club = clubService.getClubById(clubId);
        if (club == null) {
            throw new VisibleException("Club not found", HttpServletResponse.SC_NOT_FOUND);
        } else {
            JsonResponseUtil.sendJsonResponse(resp, club, HttpServletResponse.SC_OK);
        }
    }

    private void getAdminClubsForStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String studentIdParam = req.getParameter("studentId");

        if (studentIdParam == null || studentIdParam.isEmpty()) {
            throw new VisibleException("Club ID is required", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger studentId = new BigInteger(studentIdParam);
        List<Club> clubs = clubService.getAdminClubsForStudent(studentId);
        JsonResponseUtil.sendJsonResponse(resp, clubs, HttpServletResponse.SC_OK);
    }

    private void deleteClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        BigInteger clubId = new BigInteger((String) jsonMap.get("clubId"));

        if (clubService.getClubById(clubId) == null) {
            throw new VisibleException("Club with id " + clubId + " not found", HttpServletResponse.SC_NOT_FOUND);
        }

        String token = AuthUtil.extractToken(req);
        if (!authService.hasPermission(token, "CLUB_ADMIN", clubId)) {
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }

        clubService.deleteClub(clubId);
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("status", "Club successfully deleted");
        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
    }
    private void createClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        // Extract parameters from JSON
        String name = (String) jsonMap.get("name");
        BigInteger adminId = new BigInteger((String) jsonMap.get("adminId"));
        Double balance = new Double((String) jsonMap.get("balance"));
        // Call the ClubService to create the club
        Club createdClub = clubService.createClub(name, balance, adminId);

        // Build response
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("clubId", createdClub.getId());      // Include club ID
        respMap.put("clubName", createdClub.getName());  // Include club name
        respMap.put("adminId", adminId);                 // Include admin ID
        respMap.put("initialBalance", createdClub.getBalance());  // Include balance
        respMap.put("status", "Club successfully created");

        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_CREATED);
    }

    private void updateClub(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ClubRequest clubRequest = objectMapper.readValue(req.getReader(), ClubRequest.class);
        BigInteger clubId = new BigInteger(String.valueOf(clubRequest.getId()));
        System.out.println(clubId);

        String token = AuthUtil.extractToken(req);
        if (!authService.hasPermission(token, "CLUB_ADMIN", clubId)) {
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        Club updatedClub = clubService.updateClub(clubRequest);

        JsonResponseUtil.sendJsonResponse(resp, updatedClub, HttpServletResponse.SC_OK);
    }
}
