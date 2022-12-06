package com.terransky.stuffnthings.utilities;

import org.jetbrains.annotations.NotNull;

public enum CannedResponses {

    //todo: Add Responses
    INTERACTION_FAILED("An error occurred whilst executing this interaction, please submit an issue [here](%s).".formatted(Config.getErrorReportingLink())),
    GUILD_ONLY("This interaction is guild only. Please use this interaction in a guild.");

    private final String message;

    CannedResponses(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @NotNull
    public String getMessage(@NotNull Interactions interaction) {
        if (interaction == Interactions.UNKNOWN)
            throw new IllegalArgumentException("Interaction cannot be unknown.");

        return message.replace("interaction", interaction.getType());
    }
}
