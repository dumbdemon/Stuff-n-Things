package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.general.InteractionType;
import com.terransky.stuffnthings.utilities.general.Config;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Responses {

    INTERACTION_FAILED(true,
        "An error occurred whilst executing this interaction, please submit an issue [here](%s).".formatted(Config.getErrorReportingURL())),
    GUILD_ONLY(true, "This interaction is guild only. Please use this interaction in a guild."),
    NETWORK_OPERATION(false,
        String.format("An error occurred during network operation. Please try again in a few moments. If this continues, please submit an issue [here](%s).",
            Config.getErrorReportingURL()));

    private final String message;
    private final boolean isInteractionReplaceable;
    private final boolean isAllCaps;

    Responses(String message) {
        this(false, message);
    }

    Responses(boolean isInteractionReplaceable, String message) {
        this(isInteractionReplaceable, message, false);
    }

    Responses(String message, boolean isAllCaps) {
        this(false, message, isAllCaps);
    }

    Responses(boolean isInteractionReplaceable, String message, boolean isAllCaps) {
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
