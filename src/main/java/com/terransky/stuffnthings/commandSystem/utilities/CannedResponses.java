package com.terransky.stuffnthings.commandSystem.utilities;

public enum CannedResponses {

    //todo: Add Responses
    UNKNOWN(""),
    GUILD_ONLY("This interaction is guild only. Please use this interaction in a guild.");

    private final String response;

    CannedResponses(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
