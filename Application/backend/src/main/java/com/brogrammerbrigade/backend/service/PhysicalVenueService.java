package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.AddressMapper;
import com.brogrammerbrigade.backend.datasource.PhysicalVenueMapper;
import com.brogrammerbrigade.backend.domain.Address;
import com.brogrammerbrigade.backend.domain.PhysicalVenue;
import com.brogrammerbrigade.backend.dto.AddressRequest;
import com.brogrammerbrigade.backend.dto.PhysicalVenueRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class PhysicalVenueService {
    private static PhysicalVenueService instance;

    private final PhysicalVenueMapper physicalVenueMapper;
    private final AddressMapper addressMapper;
    private PhysicalVenueService() {
        this.physicalVenueMapper = PhysicalVenueMapper.getInstance();
        this.addressMapper = AddressMapper.getInstance();
    }

    // Public method to provide access to the singleton instance
    public static synchronized PhysicalVenueService getInstance() {
        if (instance == null) {
            instance = new PhysicalVenueService();
        }
        return instance;
    }
    public List<PhysicalVenue> getAllPhysicalVenues(){
        return physicalVenueMapper.getAllPhysicalVenues();

    }

    // Public method to fetch a physicalVenue by the physicalVenue id
    public PhysicalVenue getPhysicalVenueById(BigInteger id){
        return physicalVenueMapper.getPhysicalVenueById(id);
    }

    // Method to create a new PhysicalVenue with validation
    public PhysicalVenue createPhysicalVenue(PhysicalVenueRequest physicalVenueRequest) {
        Double cost = physicalVenueRequest.getCost();
        Integer venueCapacity = physicalVenueRequest.getVenueCapacity();
        String room = physicalVenueRequest.getRoom();
        String floor = physicalVenueRequest.getFloor();
        String description = physicalVenueRequest.getDescription();
        AddressRequest addressRequest = physicalVenueRequest.getAddress();

        // Validate the input parameters
        if (cost == null) {
            throw new VisibleException("Cost cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (venueCapacity == null) {
            throw new VisibleException("Venue capacity cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (room == null) {
            throw new VisibleException("There must be exactly one room.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (description == null) {
            throw new VisibleException("There must be exactly one description.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (addressRequest == null) {
            throw new VisibleException("There must be exactly one address", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (floor == null) {
            throw new VisibleException("There must be exactly one floor.", HttpServletResponse.SC_BAD_REQUEST);
        }

        BigInteger addressId = null;
        Address newAddress = null;

        if (addressRequest.getId() != null) {
            addressId = addressRequest.getId();
        } else {
            newAddress = new Address();
            newAddress.setAddressLine1(addressRequest.getAddressLine1());
            newAddress.setAddressLine2(addressRequest.getAddressLine2());
            newAddress.setCity(addressRequest.getCity());
            newAddress.setState(addressRequest.getState());
            newAddress.setCountry(addressRequest.getCountry());
            newAddress.setPostcode(addressRequest.getPostcode());

            newAddress = addressMapper.insert(newAddress);
            addressId = newAddress.getId();
        }

        // Create the PhysicalVenue object
        PhysicalVenue newPhysicalVenue = new PhysicalVenue();
        newPhysicalVenue.setDescription(description);
        newPhysicalVenue.setCost(cost);
        newPhysicalVenue.setVenueCapacity(venueCapacity);
        newPhysicalVenue.setFloor(floor);
        newPhysicalVenue.setRoom(room);
        newPhysicalVenue.setAddress(new Address(addressId));

        // Use the PhysicalVenueMapper to insert the physicalVenue into the database
        return physicalVenueMapper.insert(newPhysicalVenue);
    }

    public void deletePhysicalVenue(BigInteger physicalVenueId) {
        PhysicalVenue physicalVenue = new PhysicalVenue();
        physicalVenue.setId(physicalVenueId);

        physicalVenueMapper.delete(physicalVenue);
    }
}

