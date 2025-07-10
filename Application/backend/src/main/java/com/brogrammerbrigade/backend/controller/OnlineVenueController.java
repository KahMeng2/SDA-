package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.OnlineVenue;
import com.brogrammerbrigade.backend.dto.OnlineVenueRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.service.OnlineVenueService;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

@WebServlet(name = "OnlineVenues", urlPatterns = {"/onlinevenues/*"})
public class OnlineVenueController extends HttpServlet {
    private OnlineVenueService venueService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        venueService = OnlineVenueService.getInstance();
        objectMapper = new ObjectMapper();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            getAllOnlineVenues(req, resp);
        } else if (pathInfo.matches("/\\d+")) {
            BigInteger venueId = new BigInteger(pathInfo.substring(1));
            getOnlineVenueById(req, resp, venueId);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            createOnlineVenue(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            deleteOnlineVenue(req, resp);
        } else {
            throw new VisibleException("Resource not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getAllOnlineVenues(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<OnlineVenue> venues = venueService.getAllOnlineVenues();
        JsonResponseUtil.sendJsonResponse(resp, venues, HttpServletResponse.SC_OK);
    }

    private void getOnlineVenueById(HttpServletRequest req, HttpServletResponse resp, BigInteger venueId) throws IOException {
        OnlineVenue venue = venueService.getOnlineVenueById(venueId);
        if (venue == null) {
            throw new VisibleException("Online Venue not found", HttpServletResponse.SC_NOT_FOUND);
        } else {
            JsonResponseUtil.sendJsonResponse(resp, venue, HttpServletResponse.SC_OK);
        }
    }

    private void createOnlineVenue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        OnlineVenueRequest onlineVenueRequest = objectMapper.readValue(reader, OnlineVenueRequest.class);

        OnlineVenue createdVenue = venueService.createOnlineVenue(onlineVenueRequest);

        JsonResponseUtil.sendJsonResponse(resp, createdVenue, HttpServletResponse.SC_CREATED);
    }

    private void deleteOnlineVenue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> jsonMap = objectMapper.readValue(reader, Map.class);
        BigInteger venueId = new BigInteger(jsonMap.get("venueId").toString());

        if (venueService.getOnlineVenueById(venueId) == null) {
            throw new VisibleException("Online venue with id " + venueId + " not found", HttpServletResponse.SC_NOT_FOUND);
        }

        venueService.deleteOnlineVenue(venueId);

        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }
}