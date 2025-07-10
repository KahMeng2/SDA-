package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.OnlineVenueMapper;
import com.brogrammerbrigade.backend.domain.OnlineVenue;
import com.brogrammerbrigade.backend.dto.OnlineVenueRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.util.List;

public class OnlineVenueService {
    private static OnlineVenueService instance;
    private final OnlineVenueMapper onlineVenueMapper;

    private OnlineVenueService() {
        this.onlineVenueMapper = OnlineVenueMapper.getInstance();
    }

    public static synchronized OnlineVenueService getInstance() {
        if (instance == null) {
            instance = new OnlineVenueService();
        }
        return instance;
    }

    public List<OnlineVenue> getAllOnlineVenues() {
        return onlineVenueMapper.getAllOnlineVenues();
    }

    public OnlineVenue getOnlineVenueById(BigInteger id) {
        return onlineVenueMapper.getOnlineVenueById(id);
    }

    public OnlineVenue createOnlineVenue(OnlineVenueRequest onlineVenueRequest) {
        String description = onlineVenueRequest.getDescription();
        Double cost = onlineVenueRequest.getCost();
        Integer venueCapacity = onlineVenueRequest.getVenueCapacity();
        String link = onlineVenueRequest.getLink();


        if (description == null || link == null) {
            throw new VisibleException("Description and link are required for creating an online venue.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (cost < 0) {
            throw new VisibleException("Cost cannot be negative.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (venueCapacity <= 0) {
            throw new VisibleException("Venue capacity must be positive.", HttpServletResponse.SC_BAD_REQUEST);
        }

        OnlineVenue newOnlineVenue = new OnlineVenue();
        newOnlineVenue.setDescription(description);
        newOnlineVenue.setCost(cost);
        newOnlineVenue.setVenueCapacity(venueCapacity);
        newOnlineVenue.setLink(link);

        return onlineVenueMapper.insert(newOnlineVenue);
    }

    public void deleteOnlineVenue(BigInteger onlineVenueId) {
        if (onlineVenueId == null) {
            throw new VisibleException("Online venue ID cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        OnlineVenue onlineVenue = new OnlineVenue();
        onlineVenue.setId(onlineVenueId);

        onlineVenueMapper.delete(onlineVenue);
    }
}