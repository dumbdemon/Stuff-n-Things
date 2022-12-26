package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import org.jetbrains.annotations.NotNull;

public enum Responses {

    INTERACTION_FAILED("An error occurred whilst executing this interaction, please submit an issue [here](%s).".formatted(Config.getErrorReportingURL())),
    GUILD_ONLY("This interaction is guild only. Please use this interaction in a guild.");

    private final String message;
    private final boolean isAllCaps;

    Responses(String message) {
        this(message, false);
    }

    Responses(String message, boolean isAllCaps) {
        this.message = message;
        this.isAllCaps = isAllCaps;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAllCaps() {
        return isAllCaps;
    }

    @NotNull
    public String getMessage(@NotNull InteractionType interaction) {
        if (interaction == InteractionType.UNKNOWN)
            throw new IllegalArgumentException("Interaction cannot be unknown.");

        return getMessage().replaceAll("(?i)interaction", isAllCaps() ? interaction.getName().toUpperCase() : interaction.getName());
    }
}
