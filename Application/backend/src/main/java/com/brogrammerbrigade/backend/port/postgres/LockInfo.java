package com.brogrammerbrigade.backend.port.postgres;

import java.time.Instant;

public class LockInfo {
    private String owner;
    private Instant expirationTime;

    public LockInfo(String owner) {
        this.owner = owner;
        this.expirationTime = Instant.now().plusSeconds(3600); // One hour from now
    }

    public String getOwner() {
        return owner;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expirationTime);
    }
}
