package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.datasource.AddressMapper;
import com.brogrammerbrigade.backend.domain.Address;
import com.brogrammerbrigade.backend.dto.AddressRequest;
import com.brogrammerbrigade.backend.dto.PhysicalVenueRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.domain.PhysicalVenue;
import com.brogrammerbrigade.backend.service.PhysicalVenueService;
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

@WebServlet(name = "PhysicalVenues", urlPatterns = {"/physicalvenues/*"})
public class PhysicalVenueController extends HttpServlet {
    private PhysicalVenueService venueService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        // Not sure if it should be singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        // load the driver
        venueService = PhysicalVenueService.getInstance();

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
            getAllPhysicalVenues(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            BigInteger venueId = new BigInteger(pathInfo.substring(1));
            getPhysicalVenueById(req, resp, venueId);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            createPhysicalVenue(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            deletePhysicalVenue(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getAllPhysicalVenues(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        List<PhysicalVenue> venues = venueService.getAllPhysicalVenues();
        JsonResponseUtil.sendJsonResponse(resp, venues, HttpServletResponse.SC_OK);
    }

    private void getPhysicalVenueById(HttpServletRequest req, HttpServletResponse resp, BigInteger venueId) throws IOException {
        PhysicalVenue venue = venueService.getPhysicalVenueById(venueId);
        if (venue == null) {
            throw new VisibleException("PhysicalVenue not found", HttpServletResponse.SC_NOT_FOUND);
        } else {
            JsonResponseUtil.sendJsonResponse(resp, venue, HttpServletResponse.SC_OK);
        }
    }

    private void deletePhysicalVenue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        // Extract parameters from JSON
        BigInteger venueId = new BigInteger((String) jsonMap.get("venueId"));

        if (venueService.getPhysicalVenueById(venueId) == null) {
            throw new VisibleException("Physical venue with id " + venueId + " not found", HttpServletResponse.SC_NOT_FOUND);
        }

        venueService.deletePhysicalVenue(venueId);

        // Convert the custom response map to JSON string
        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void createPhysicalVenue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        PhysicalVenueRequest physicalVenueRequest = objectMapper.readValue(reader, PhysicalVenueRequest.class);

        PhysicalVenue createdPhysicalVenue = venueService.createPhysicalVenue(physicalVenueRequest);

        // Convert the custom response map to JSON string
        JsonResponseUtil.sendJsonResponse(resp, createdPhysicalVenue, HttpServletResponse.SC_CREATED);
    }
}
