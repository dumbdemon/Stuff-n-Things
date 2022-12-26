package com.terransky.stuffnthings.interfaces.discordInteractions;

import com.terransky.stuffnthings.utilities.general.Interactions;
import org.jetbrains.annotations.NotNull;

public interface IInteraction extends Comparable<IInteraction> {

    /**
     * The name or ID reference of this bot element.
     *
     * @return A {@link String} of this bot element.
     */
    String getName();

    Interactions getInteractionType();

    @Override
    default int compareTo(@NotNull IInteraction iInteraction) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), iInteraction.getName()) &
            String.CASE_INSENSITIVE_ORDER.compare(getInteractionType().getName(), iInteraction.getInteractionType().getName());
    }
}
