package com.brogrammerbrigade.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class ClubRequest {
    private BigInteger id;
    private String name;
    private Double balance;

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {this.balance = balance;}
}
