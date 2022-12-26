package com.terransky.stuffnthings.interfaces.discordInteractions;

import org.jetbrains.annotations.NotNull;

public interface IInteraction extends Comparable<IInteraction> {

    /**
     * The name or ID reference of this bot element.
     *
     * @return A {@link String} of this bot element.
     */
    String getName();

    @Override
    default int compareTo(@NotNull IInteraction iInteraction) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), iInteraction.getName());
    }
}