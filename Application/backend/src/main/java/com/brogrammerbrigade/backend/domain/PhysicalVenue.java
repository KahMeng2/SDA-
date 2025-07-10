package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.EventMapper;
import com.brogrammerbrigade.backend.datasource.PhysicalVenueMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.List;

public class PhysicalVenue extends Venue {
    private BigInteger id;
    private Address address;
    private String floor;
    private String room;

    public PhysicalVenue() {}

    @Override
    public void load() {
        // Use mapper.
        PhysicalVenueMapper mapper = PhysicalVenueMapper.getInstance();
        PhysicalVenue physicalVenueData = mapper.getPhysicalVenueById(this.id);
        super.loadParent(physicalVenueData);
        if (address == null){
            setAddress(physicalVenueData.getAddress());
        }
        if (floor == null){
            setFloor(physicalVenueData.getFloor());
        }
        if (room == null){
            setRoom(physicalVenueData.getRoom());
        }
    }

    public PhysicalVenue(BigInteger id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public List<Event> getEvents() {
        if (this.events == null){
            super.setEvents(EventMapper.getInstance().getEventsForVenue(this.id,false));
        }
        return this.events;
    }

    public String getFloor() {
        if (this.floor == null){
            load();
        }
        return floor;
    }

    public String getRoom() {
        if (this.room == null){
            load();
        }
        return room;
    }

    public Address getAddress() {
        if (this.address == null){
            load();
        }
        return address;
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    public BigInteger getAddressId() {
        return getAddress().getId();
    }

    public void setAddressId(BigInteger addressId) {
        getAddress().setId(addressId);
    }
}
