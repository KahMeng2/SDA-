package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.domain.Ticket;
import com.brogrammerbrigade.backend.dto.RsvpRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.RsvpService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RSVP", urlPatterns = {"/rsvps/*"})
public class RsvpController extends HttpServlet {
    private RsvpService rsvpService;
    private ObjectMapper objectMapper;
    private AuthenticationService authService;
    @Override
    public void init() throws ServletException {
        // Not sure if it should be singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        // load the driver
        rsvpService = RsvpService.getInstance();
        // Initialize AuthenticationService
        authService = AuthenticationService.getInstance();

//        authService = new AuthenticationService(userService, TokenServiceFactory.createTokenService());
        // Initialise object mapper for json
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures dates are serialized as ISO-8601 strings

        super.init();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getRsvp(req, resp);
        } else if (pathInfo.equals("/forStudent")) {
            getRsvpsForStudent(req, resp);
        } else if (pathInfo.equals("/forEvent")) {
            getRsvpsForEvent(req, resp);
        } else if (pathInfo.equals("/ticketsForStudent")) {
            // TODO move this endpoint to a new ticketController
            getTicketsForStudent(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            createRsvp(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
        // User Story 2: RSVP to an event
        // Input: student ids(RSVP by), and event id.
        // Return a ticket???

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            updateRsvpTickets(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            deleteRsvp(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
        
        // User Storu 8: Cancel RSVP or ticket to event.
        // Might need to implement two endpoints for this user story, since we can either
        // 1) Delete the entire RSVP
        // 2) Delete some tickets linked to the RSVP. -> might be a put request.
    }

    private void getRsvp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Extract rsvpStudentId from the URL query parameter
        String rsvpStudentIdParam = req.getParameter("rsvpStudentId");
        String eventIdParam = req.getParameter("eventId");

        if (rsvpStudentIdParam == null || rsvpStudentIdParam.isEmpty()) {
            throw new VisibleException("rsvpStudentId is required", HttpServletResponse.SC_NOT_FOUND);
        }
        if (eventIdParam == null || eventIdParam.isEmpty()) {
            throw new VisibleException("eventId is required", HttpServletResponse.SC_NOT_FOUND);
        }

        BigInteger rsvpStudentId = new BigInteger(rsvpStudentIdParam);
        BigInteger eventId = new BigInteger(eventIdParam);

        Rsvp rsvp = rsvpService.getRsvp(rsvpStudentId, eventId);

        if (rsvp == null) {
            throw new VisibleException("RSVP not found", HttpServletResponse.SC_NOT_FOUND);
        } else {
            Map<String, Object> respMap = new HashMap<>();
            respMap.put("rsvpStudentId", rsvp.getRsvpStudentId());
            respMap.put("eventId", rsvp.getEventId());
            respMap.put("dateCreated", rsvp.getDateCreated());
            respMap.put("tickets", rsvp.getTickets());

            JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
        }
    }

    private void getRsvpsForStudent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Extract rsvpStudentId from the URL query parameter
        String rsvpStudentIdParam = req.getParameter("rsvpStudentId");

        if (rsvpStudentIdParam == null || rsvpStudentIdParam.isEmpty()) {
            throw new VisibleException("rsvpStudentId is required", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger rsvpStudentId = new BigInteger(rsvpStudentIdParam);

        // Call the service to get RSVPs for the student
        List<Rsvp> rsvps = rsvpService.getRsvpsForStudent(rsvpStudentId);

        JsonResponseUtil.sendJsonResponse(resp, rsvps, HttpServletResponse.SC_OK);
    }


    private void getRsvpsForEvent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventIdParam = req.getParameter("eventId");
        BigInteger eventId = new BigInteger(eventIdParam);

        List<Rsvp> rsvps = rsvpService.getRsvpsForEvent(eventId);

        JsonResponseUtil.sendJsonResponse(resp, rsvps, HttpServletResponse.SC_OK);
    }

    public void getTicketsForStudent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentIdParam = req.getParameter("studentId");
        BigInteger studentId = new BigInteger(studentIdParam);

        List<Ticket> tickets = rsvpService.getTicketsForStudent(studentId);

        JsonResponseUtil.sendJsonResponse(resp, tickets, HttpServletResponse.SC_OK);
    }

    private void createRsvp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Read the request body and convert it into the RsvpRequest DTO
        RsvpRequest rsvpRequest = objectMapper.readValue(req.getReader(), RsvpRequest.class);
        BigInteger studentId = new BigInteger(String.valueOf(rsvpRequest.getRsvpStudentId()));
        String token = AuthUtil.extractToken(req);
        if (!authService.isAuthorized(token, studentId)) {
            throw new VisibleException("You cannot create an RSVP as another student", HttpServletResponse.SC_FORBIDDEN);
        }
        // Call the RsvpService to create the RSVP
        Rsvp createdRsvp = rsvpService.createRsvp(rsvpRequest);

        // Build response map
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("rsvpStudentId", createdRsvp.getRsvpStudentId());
        respMap.put("eventId", createdRsvp.getEventId());
        respMap.put("dateCreated", createdRsvp.getDateCreated());
        respMap.put("tickets", createdRsvp.getTickets());

        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_CREATED);
    }

    private void updateRsvpTickets(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Read the request body and convert it into the RsvpRequest DTO
        RsvpRequest rsvpRequest = objectMapper.readValue(req.getReader(), RsvpRequest.class);
        BigInteger studentId = new BigInteger(String.valueOf(rsvpRequest.getRsvpStudentId()));
        System.out.println(studentId);
        String token = AuthUtil.extractToken(req);
        if (!authService.isAuthorized(token, studentId)) {
            throw new VisibleException("You are not authorized to update this ticket", HttpServletResponse.SC_FORBIDDEN);
        }
        // Call the RsvpService to update the RSVP
        Rsvp updatedRsvp = rsvpService.updateRsvpTickets(rsvpRequest);

        JsonResponseUtil.sendJsonResponse(resp, updatedRsvp, HttpServletResponse.SC_OK);
    }

    private void deleteRsvp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        // Extract parameters from JSON
        BigInteger rsvpStudentId = new BigInteger((String) jsonMap.get("rsvpStudentId"));
        BigInteger eventId = new BigInteger((String) jsonMap.get("eventId"));
        System.out.println(rsvpStudentId);
        String token = AuthUtil.extractToken(req);

        if (rsvpService.getRsvp(rsvpStudentId, eventId) == null) {
            throw new VisibleException("This rsvp does not exist", HttpServletResponse.SC_NOT_FOUND);
        }

        if (!authService.isAuthorized(token, rsvpStudentId)) {
            throw new VisibleException("You are not authorized to delete this ticket", HttpServletResponse.SC_FORBIDDEN);
        }
        rsvpService.deleteRsvp(rsvpStudentId, eventId);
        // Build response
        Map<String, Object> respMap = new HashMap<>();

        JsonResponseUtil.sendJsonResponse(resp, respMap, HttpServletResponse.SC_OK);
    }
}
