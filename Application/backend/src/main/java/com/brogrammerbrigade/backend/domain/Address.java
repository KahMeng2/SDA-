package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.AddressMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class Address extends DomainObject {
    private BigInteger id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private Integer postcode;

    // Constructor for lazy loading
    public Address(BigInteger id) {
        this.id = id;
    }


    // Default constructor
    public Address() {
        // Call unit of work here if needed
    }

    // Getters and Setters
    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonProperty("addressLine1")
    public String getAddressLine1() {
        if (addressLine1 == null) {
            load();
        }
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @JsonProperty("addressLine2")
    public String getAddressLine2() {
        if (addressLine2 == null) {
            load();
        }
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        if (addressLine2 == null){
            this.addressLine2 = "";
        } else {
            this.addressLine2 = addressLine2;
        }
    }

    @JsonProperty("city")
    public String getCity() {
        if (city == null) {
            load();
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state")
    public String getState() {
        if (state == null) {
            load();
        }
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("country")
    public String getCountry() {
        if (country == null) {
            load();
        }
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("postcode")
    public Integer getPostcode() {
        if (postcode == null) {
            load();
        }
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    private void load() {
        if (this.id != null) {
            AddressMapper addressMapper = AddressMapper.getInstance();
            Address loadedAddress = addressMapper.getAddressById(this.id);

            if (loadedAddress != null) {
                this.addressLine1 = loadedAddress.getAddressLine1();
                this.addressLine2 = loadedAddress.getAddressLine2();
                this.city = loadedAddress.getCity();
                this.state = loadedAddress.getState();
                this.country = loadedAddress.getCountry();
                this.postcode = loadedAddress.getPostcode();
            }
        }
    }
}