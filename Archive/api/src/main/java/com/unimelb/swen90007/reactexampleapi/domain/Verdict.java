package com.unimelb.swen90007.reactexampleapi.domain;

public class Verdict {
    private long supporting;
    private long opposing;

    public Verdict(long supporting, long opposing) {
        this.supporting = supporting;
        this.opposing = opposing;
    }

    // Getters
    public long getSupporting() { return supporting; }
    public long getOpposing() { return opposing; }
}