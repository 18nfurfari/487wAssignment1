package com.example.assignment1487wgui;

public class EntryRecord {
    private final Integer actionId;
    private final Integer psuId;
    private final String role;
    private final String entryTime;
    private final String departureTime;


    public EntryRecord(int actionId, int id, String role, String entryTime, String departureTime) {
        this.actionId = actionId;
        this.psuId = id;
        this.role = role;
        this.entryTime = entryTime;
        this.departureTime = departureTime;
    }

    public Integer getActionId() {
        return actionId;
    }

    public Integer getPsuId() {
        return psuId;
    }

    public String getRole() {
        return role;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }
}
