package com.terransky.stuffnthings.interfaces;

import org.jetbrains.annotations.NotNull;

public interface IInteractionElement extends Comparable<IInteractionElement> {

    /**
     * The name or ID reference of this bot element.
     *
     * @return A {@link String} of this bot element.
     */
    String getName();

    @Override
    default int compareTo(@NotNull IInteractionElement iInteractionElement) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), iInteractionElement.getName());
    }
}
