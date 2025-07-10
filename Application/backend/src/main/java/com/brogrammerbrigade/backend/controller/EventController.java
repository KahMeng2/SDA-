package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.dto.EventFilterRequest;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.EventService;
import com.brogrammerbrigade.backend.util.AuthUtil;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.util.List;
import java.util.Map;

@WebServlet(name = "Events", urlPatterns = {"/events/*"})
public class EventController extends HttpServlet {
    // event mapper, event service, object mapper attributes
    private EventService eventService;
    private ObjectMapper objectMapper;
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        // Not sure if it should be singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        // load the driver
        eventService = EventService.getInstance();
        // Initialize AuthenticationService
        authService = AuthenticationService.getInstance();

//        authService = new AuthenticationService(userService, TokenServiceFactory.createTokenService());
        // Initialise object mapper for json
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures dates are serialized as ISO-8601 strings
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        super.init();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllEvents(req, resp);
        } else if (pathInfo.equals("/forClub")) {
            getEventsForClub(req, resp);
        } else if (pathInfo.equals("/forVenue")) {
            getEventsForVenue(req, resp);
        } else if (pathInfo.equals("/filter")) {
            getFilteredEvents(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            BigInteger eventId = new BigInteger(pathInfo.substring(1));
            getEventById(req, resp, eventId);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            createEvent(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
        // User Story 3: Create an event for a Student event that the student administers
        // Input: StudentID -> auth that student is admin for event
        // Should check if event has enough budget.

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.equals("/executeUpdate")) {
            updateEvent(req, resp);
        } else if (pathInfo.equals("/cancel")) {
            cancelEvent(req, resp);
        } else if(pathInfo.equals("/getResource")){
            getUpdateResource(req, resp);
        } else if(pathInfo.equals("/releaseResource")){
            releaseUpdateResource(req, resp);
        }
        else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }

        // User Story 9: Update event
        // Input: StudentID, eventID, Event Parameters.
        // auth student if is admin.

    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            deleteEvent(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }

        // User Story 4: Remove an event for a Student event that the student administers
        // Input: StudentID -> auth that student is admin for event
        // Should Also remove all existing RSVPs and Tickets that link to the event?
    }

    private void getAllEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        List<Event> events = eventService.getAllEvents();
        JsonResponseUtil.sendJsonResponse(resp, events, HttpServletResponse.SC_OK);
    }

    private void getEventById(HttpServletRequest req, HttpServletResponse resp, BigInteger eventId) throws IOException {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new VisibleException("Event not found", HttpServletResponse.SC_NOT_FOUND);
        } else {
            JsonResponseUtil.sendJsonResponse(resp, event, HttpServletResponse.SC_OK);
        }
    }

    private void getEventsForClub(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Extract clubId from the URL query parameter
        String clubIdParam = req.getParameter("clubId");

        if (clubIdParam == null || clubIdParam.isEmpty()) {
            throw new VisibleException("Club ID is required", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger clubId = new BigInteger(clubIdParam);

        // Call the service to get events for the club
        List<Event> events = eventService.getEventsForClub(clubId);
        JsonResponseUtil.sendJsonResponse(resp, events, HttpServletResponse.SC_OK);
    }

    private void getEventsForVenue(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            // Extract venueId and isOnline from the URL query parameters
            String venueIdParam = req.getParameter("venueId");
            String isOnlineParam = req.getParameter("isOnline");

            if (venueIdParam == null || venueIdParam.isEmpty() || isOnlineParam == null) {
                throw new VisibleException("Venue ID and isOnline are required", HttpServletResponse.SC_BAD_REQUEST);
            }

            BigInteger venueId = new BigInteger(venueIdParam);
            Boolean isOnline = Boolean.parseBoolean(isOnlineParam);

            // Call the service to get events for the venue
            List<Event> events = eventService.getEventsForVenue(venueId, isOnline);
            JsonResponseUtil.sendJsonResponse(resp, events, HttpServletResponse.SC_OK);
    }


    private void createEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            BufferedReader reader = req.getReader();
            EventRequest eventRequest = objectMapper.readValue(reader, EventRequest.class);
            // Check if the user is an admin of the club
            BigInteger clubId = new BigInteger(String.valueOf(eventRequest.getClubId()));

            String token = AuthUtil.extractToken(req);
            if (!authService.hasPermission(token, "CLUB_ADMIN", clubId)) {
                throw new VisibleException("You are not authorized to create events for this club", HttpServletResponse.SC_FORBIDDEN);
            }
            Event createdEvent = eventService.createEvent(eventRequest);

            // Convert the custom response map to JSON string
            JsonResponseUtil.sendJsonResponse(resp, createdEvent, HttpServletResponse.SC_CREATED);
    }

    private void getUpdateResource(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        EventRequest eventRequest = objectMapper.readValue(reader, EventRequest.class);

        // Ensure the event ID is passed in the request
        String eventIdString = req.getParameter("eventId");
        if (eventIdString == null || eventIdString.isEmpty()) {
            throw new VisibleException("Event ID cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger eventId = new BigInteger(eventIdString);
        String username;
        String token = AuthUtil.extractToken(req);
        if (!(authService.isEventAdmin(token, "CLUB_ADMIN", eventId))){
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        username = authService.getUsernameFromToken(token);
        if (username==null) {
            throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


        eventService.getUpdateEventResource(eventId,eventRequest,username);
        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void releaseUpdateResource(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        EventRequest eventRequest = objectMapper.readValue(reader, EventRequest.class);

        // Ensure the event ID is passed in the request
        String eventIdString = req.getParameter("eventId");
        if (eventIdString == null || eventIdString.isEmpty()) {
            throw new VisibleException("Event ID cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger eventId = new BigInteger(eventIdString);
        String username;
        String token = AuthUtil.extractToken(req);
        if (!(authService.isEventAdmin(token, "CLUB_ADMIN", eventId))){
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        username = authService.getUsernameFromToken(token);
        if (username==null) {
            throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


        eventService.releaseUpdateEventResource(eventId,eventRequest,username);
        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void updateEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        EventRequest eventRequest = objectMapper.readValue(reader, EventRequest.class);

        // Ensure the event ID is passed in the request
        String eventIdString = req.getParameter("eventId");
        if (eventIdString == null || eventIdString.isEmpty()) {
            throw new VisibleException("Event ID cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }
        BigInteger eventId = new BigInteger(eventIdString);
        String username;
        String token = AuthUtil.extractToken(req);
        if (!(authService.isEventAdmin(token, "CLUB_ADMIN", eventId))){
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        username = authService.getUsernameFromToken(token);
        if (username==null) {
            throw new VisibleException("Username field is missing", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


        Event updatedEvent = eventService.updateEvent(eventId, eventRequest, username);

        JsonResponseUtil.sendJsonResponse(resp, updatedEvent, HttpServletResponse.SC_OK);
    }

    private void cancelEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String eventIdString = req.getParameter("eventId");

        if (eventIdString == null || eventIdString.isEmpty()) {
            throw new VisibleException("Event ID cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger eventId = new BigInteger(eventIdString);

        String token = AuthUtil.extractToken(req);
        if (!authService.isEventAdmin(token, "CLUB_ADMIN", eventId)) {
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        // Call the service to cancel the event
        Event cancelledEvent = eventService.cancelEvent(eventId);

        JsonResponseUtil.sendJsonResponse(resp, cancelledEvent, HttpServletResponse.SC_OK);
    }


    private void deleteEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Parse JSON body to get the eventId
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);

        // Extract eventId from the JSON body
        String eventIdParam = (String) jsonMap.get("eventId");

        if (eventIdParam == null || eventIdParam.isEmpty()) {
            throw new VisibleException("Event ID and Club ID cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger eventId = new BigInteger(eventIdParam);

        if (eventService.getEventById(eventId) == null) {
            throw new VisibleException("Event " + eventId + " doesn't exist", HttpServletResponse.SC_NOT_FOUND);
        }

        String token = AuthUtil.extractToken(req);
        if (!authService.isEventAdmin(token, "CLUB_ADMIN", eventId)) {
            throw new VisibleException("Permission denied", HttpServletResponse.SC_FORBIDDEN);
        }
        // Call the EventService to delete the event
        eventService.deleteEvent(eventId);
        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void getFilteredEvents(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Parse the incoming JSON request body into an EventFilterRequest object
        // Get query parameters directly from the request
        String nameQuery = req.getParameter("nameQuery");
        Boolean isUpcoming = Boolean.parseBoolean(req.getParameter("isUpcoming"));
        Boolean isCancelled = Boolean.parseBoolean(req.getParameter("isCancelled"));
        Boolean isOnline = Boolean.parseBoolean(req.getParameter("isOnline"));

        // Create an EventFilterRequest object manually with the retrieved parameters
        EventFilterRequest filterRequest = new EventFilterRequest();
        filterRequest.setNameQuery(nameQuery);
        filterRequest.setOnline(isOnline);
        filterRequest.setUpcoming(isUpcoming);
        filterRequest.setCancelled(isCancelled);

        // Use the eventService to filter events based on the filterRequest parameters
        List<Event> events = eventService.getFilteredEvents(filterRequest);

        // Serialize the filtered list of events to JSON and write to the response
        String jsonResponse = objectMapper.writeValueAsString(events);
        JsonResponseUtil.sendJsonResponse(resp, events, HttpServletResponse.SC_OK);
    }
}
