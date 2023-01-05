package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.IInteractionType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Responses {

    INTERACTION_FAILED(true,
        "An error occurred whilst executing this interaction, please submit an issue [here](%s).".formatted(Config.getErrorReportingURL())),
    GUILD_ONLY(true, "This interaction is guild only. Please use this interaction in a guild.");

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
    public String getMessage(@NotNull IInteractionType interaction) {
        if (!isInteractionReplaceable())
            return getMessage();

        if (interaction == IInteractionType.UNKNOWN)
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
