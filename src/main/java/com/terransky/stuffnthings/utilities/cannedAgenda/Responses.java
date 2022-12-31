package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import org.jetbrains.annotations.NotNull;

public enum Responses {

    INTERACTION_FAILED("An error occurred whilst executing this interaction, please submit an issue [here](%s).".formatted(Config.getErrorReportingURL()),
        true),
    GUILD_ONLY("This interaction is guild only. Please use this interaction in a guild.", true);

    private final String message;
    private final boolean isInteractionReplaceable;
    private final boolean isAllCaps;

    Responses(String message) {
        this(message, true);
    }

    Responses(String message, boolean isInteractionReplaceable) {
        this(message, isInteractionReplaceable, false);
    }

    Responses(String message, boolean isInteractionReplaceable, boolean isAllCaps) {
        this.message = message;
        this.isInteractionReplaceable = isInteractionReplaceable;
        this.isAllCaps = isAllCaps;
    }

    public String getMessage() {
        return message;
    }

    @NotNull
    public String getMessage(@NotNull InteractionType interaction) {
        if (!isInteractionReplaceable())
            return getMessage();

        if (interaction == InteractionType.UNKNOWN)
            throw new IllegalArgumentException("Interaction cannot be unknown.");

        String name = interaction.getName();
        return getMessage().replaceAll("(?i)interaction", isAllCaps() ? name.toUpperCase() : name);
    }

    public boolean isAllCaps() {
        return isAllCaps;
    }

    public boolean isInteractionReplaceable() {
        return isInteractionReplaceable;
    }
}
