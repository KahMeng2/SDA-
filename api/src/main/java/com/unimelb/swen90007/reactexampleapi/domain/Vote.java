package com.unimelb.swen90007.reactexampleapi.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;

public class Vote {
    private String id;
    private String name;
    private String email;
    private boolean supporting;
    private Status status;
    private OffsetDateTime created;
    private boolean _new = true;

    // accessors, omitted for brevity ...

    @JsonIgnore
    public boolean isNew() {
        return _new;
    }

    @JsonIgnore
    public void setNew(boolean _new) {
        this._new = _new;
    }

    public static enum Status {
        UNVERIFIED,
        ACCEPTED,
        REJECTED
    }
}