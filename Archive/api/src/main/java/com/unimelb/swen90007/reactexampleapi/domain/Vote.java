package com.unimelb.swen90007.reactexampleapi.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class Vote {
    private String id;
    private String name;
    private String email;
    private boolean supporting;
    private Status status;
    private OffsetDateTime created;
    @JsonIgnore
    private boolean _new = true;

    // Default constructor
    public Vote() {}

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("supporting")
    public boolean isSupporting() {
        return supporting;
    }

    public void setSupporting(boolean supporting) {
        this.supporting = supporting;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("created")
    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    private boolean isNew = true;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    // Status enum
    public static enum Status {
        UNVERIFIED,
        ACCEPTED,
        REJECTED
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", supporting=" + supporting +
                ", status=" + status +
                ", created=" + created +
                ", _new=" + _new +
                '}';
    }
}