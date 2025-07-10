package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.Review;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.FundingApplicationService;
import com.brogrammerbrigade.backend.service.UserService;
import com.brogrammerbrigade.backend.util.AuthUtil;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FundingApplication", urlPatterns = {"/funding_application/*"})
public class FundingApplicationController extends HttpServlet {
    private FundingApplicationService fundingApplicationService;
    private ObjectMapper objectMapper;
    private AuthenticationService authService;
    //    private AuthenticationService authService;
    @Override
    public void init() throws ServletException {
        // Singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        // load the driver
        fundingApplicationService = FundingApplicationService.getInstance();
        authService = AuthenticationService.getInstance();
        // Initialize AuthenticationService
        UserService userService = UserService.getInstance();
//        authService = new AuthenticationService(userService, TokenServiceFactory.createTokenService());
        // Initialise object mapper for json
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures dates are serialized as ISO-8601 strings
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllFundingApplications(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1));
            getFundingApplicationById(req, resp, applicationID);
        } else if (pathInfo.matches("/club/\\d+")) {
            BigInteger clubId = new BigInteger(pathInfo.substring(6));
            getFundingApplicationsByClubId(req, resp, clubId);
        } else if (pathInfo.matches("/\\d+/reviews")) {
            BigInteger applicationId = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            getReviewsForApplication(req, resp, applicationId);
        } else if (pathInfo.matches("/allreviews")) {
            getAllReviews(req, resp);
        } else {
            JsonResponseUtil.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // User Story 7: Create a funding application
        // Input: studentid, clubid
        // Check auth

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            createFundingApplication(req, resp);
        } else if (pathInfo.matches("/\\d+/submit")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            submitFundingApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/review")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            startReview(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/approve")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            approveApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/reject")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            rejectApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/cancel")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            cancelApplication(req, resp, applicationID);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.matches("/\\d+/startEdit")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            startEditApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/commitEdit")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            commitEditApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/stopEdit")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            stopEditApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/cancel")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            cancelApplication(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/submitReview")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            submitReview(req, resp, applicationID);
        } else if (pathInfo.matches("/\\d+/cancelReview")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1, pathInfo.lastIndexOf("/")));
            stopReview(req, resp, applicationID);
        } else {
            JsonResponseUtil.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+")) {
            BigInteger applicationID = new BigInteger(pathInfo.substring(1));
            deleteFundingApplication(req, resp, applicationID);
        } else {
            JsonResponseUtil.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getAllFundingApplications(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long startTime = System.nanoTime();
            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){

                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            ArrayList<FundingApplicationContext> applications = fundingApplicationService.getAllFundingApplications();
            JsonResponseUtil.sendJsonResponse(resp, applications, HttpServletResponse.SC_OK);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println(duration/1000000);
    }

    private void getFundingApplicationById(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, true))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            FundingApplicationContext application = fundingApplicationService.getFundingApplicationById(applicationID);
            if (application == null) {
                throw new VisibleException("Funding application not found", HttpServletResponse.SC_NOT_FOUND);
            } else {
                JsonResponseUtil.sendJsonResponse(resp, application, HttpServletResponse.SC_OK);
            }
    }

    private void getFundingApplicationsByClubId(HttpServletRequest req, HttpServletResponse resp, BigInteger clubId) throws IOException {
            String token = AuthUtil.extractToken(req);
            if (!(authService.isClubAdmin(token, clubId, true))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            List<FundingApplicationContext> applications = fundingApplicationService.getFundingApplicationsByClubId(clubId);
            JsonResponseUtil.sendJsonResponse(resp, applications, HttpServletResponse.SC_OK);
    }

    private void createFundingApplication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            BufferedReader reader = req.getReader();
            Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);

            // Extract values from the request
            BigInteger clubID = new BigInteger((String) jsonMap.get("clubId"));
            String description = (String) jsonMap.get("description");
            Double amount = Double.valueOf((String) jsonMap.get("amount"));
            Integer semester = Integer.valueOf((String) jsonMap.get("semester"));
            Year year = Year.of(Integer.parseInt((String) jsonMap.get("year")));

            String token = AuthUtil.extractToken(req);
            if (!(authService.isClubAdmin(token, clubID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }

            FundingApplicationContext createdApplication = fundingApplicationService.createFundingApplication(clubID, description, amount, semester, year);

            Map<String, Object> respMap = new HashMap<>();
            respMap.put("applicationId", createdApplication.getId());
            respMap.put("status", "Funding application successfully created");

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_CREATED);
    }

    private void submitFundingApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            fundingApplicationService.submitApplication(applicationID);

            Map<String, Object> respMap = new HashMap<>();
            respMap.put("status", "Funding application successfully submitted");

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
    }

    private void startEditApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {
            String username;

            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            username = authService.getUsernameFromToken(token);
            if (username==null) {
                throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            fundingApplicationService.startEditFundingApplication(applicationID, username);

            JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Another user is editing the funding application: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void stopEditApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {

            String username;
            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            username = authService.getUsernameFromToken(token);
            if (username==null) {
                throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            fundingApplicationService.stopEditFundingApplication(applicationID, username);

            JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Another user is editing the funding application: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void cancelApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {
            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            String username = authService.getUsernameFromToken(token);
            if (username==null) {
                throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            fundingApplicationService.cancelApplication(applicationID, username);

            Map<String, Object> respMap = new HashMap<>();
            respMap.put("status", "Funding application cancelled");
            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Unable to cancel funding application: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void commitEditApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {
            Map<String, Object> updates = objectMapper.readValue(req.getReader(), Map.class);
            String username;

            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            username = authService.getUsernameFromToken(token);
            if (username==null) {
                throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            FundingApplicationContext updatedApplication = fundingApplicationService.commitEditFundingApplication(applicationID, updates, username);

            JsonResponseUtil.sendJsonResponse(resp, updatedApplication, HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Unable to edit funding application: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteFundingApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID, false))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            fundingApplicationService.deleteFundingApplication(applicationID);
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("status", "Funding application successfully deleted");

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
    }

    private void startReview(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationId) throws IOException {
        try {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }

            // Extract facultyMemberId from the request
            BigInteger facultyMemberId =authService.getIdFromToken(token);

            fundingApplicationService.startReview(applicationId, facultyMemberId);

            JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Another faculty member has started review on application: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void stopReview(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationId) throws IOException {
        try {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }

            BigInteger facultyMemberId =authService.getIdFromToken(token);
            fundingApplicationService.stopReview(applicationId, facultyMemberId);

            JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Failed to stop review: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void submitReview(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationId) throws IOException {
        try {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            Map<String, Object> reviewData = objectMapper.readValue(req.getReader(), Map.class);

            // Extract facultyMemberId from the request

            BigInteger facultyMemberId =authService.getIdFromToken(token);
            Review createdReview = fundingApplicationService.submitReview(applicationId, facultyMemberId, reviewData);

            JsonResponseUtil.sendJsonResponse(resp, createdReview, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Unable to create review " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void approveApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            fundingApplicationService.approveApplication(applicationID);
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("status", "Funding application approved");

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Unable to approve funding application", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void rejectApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {
        try {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            fundingApplicationService.rejectApplication(applicationID);
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("status", "Funding application rejected");

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonResponseUtil.sendErrorResponse(resp, "Unable to reject funding application", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void getAllReviews(HttpServletRequest req, HttpServletResponse resp) throws IOException {

            String token = AuthUtil.extractToken(req);
            if (!(authService.getUserTypeFromToken(token).equals("FACULTY"))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            List<Review> reviews = fundingApplicationService.getAllReviews();
            JsonResponseUtil.sendJsonResponse(resp, reviews, HttpServletResponse.SC_OK);
    }

    private void getReviewsForApplication(HttpServletRequest req, HttpServletResponse resp, BigInteger applicationID) throws IOException {

            String token = AuthUtil.extractToken(req);
            if (!(authService.isFundingApplicationAdmin(token, applicationID,true))){
                throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
            }
            FundingApplicationContext application = fundingApplicationService.getFundingApplicationById(applicationID);
            if (application == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Funding application not found");

            }
            List<Review> reviews = fundingApplicationService.getReviewsForApplication(application);
            JsonResponseUtil.sendJsonResponse(resp, reviews, HttpServletResponse.SC_OK);
    }
}