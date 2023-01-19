package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

import org.jetbrains.annotations.NotNull;

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

    public static Status getStatusByState(@NotNull String state) {
        switch (state) {
            case "current" -> {
                return CURRENT;
            }
            case "finished" -> {
                return FINISHED;
            }
            case "tba" -> {
                return TBA;
            }
            case "unreleased" -> {
                return UNRELEASED;
            }
            case "upcoming" -> {
                return UPCOMING;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }

    public String getState() {
        return state;
    }
}
