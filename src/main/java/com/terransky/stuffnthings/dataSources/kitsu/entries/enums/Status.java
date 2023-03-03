package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

@SuppressWarnings("unused")
public enum Status {

    UNKNOWN("UNKNOWN"),
    CURRENT("Current"),
    FINISHED("Finished"),
    TBA("To Be Announced"),
    UNRELEASED("Unreleased"),
    UPCOMING("Upcoming");

    private final String state;

    Status(String state) {
        this.state = state;
    }

    public static Status getStatusByState(String state) {
        if (state == null || state.isEmpty())
            return UNKNOWN;

        try {
            return Enum.valueOf(Status.class, state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public String getState() {
        return state;
    }
}
