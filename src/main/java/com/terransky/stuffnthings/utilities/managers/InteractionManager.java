package com.terransky.stuffnthings.utilities.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;

public class InteractionManager<T extends IInteraction<?>> {
    protected HashSet<T> interactions = new HashSet<>();

    protected InteractionManager() {
    }

    protected final void addInteraction(@NotNull T interaction) {
        interactions.add(interaction);
    }

    public Optional<T> getInteraction(String interactionName) {
        return interactions.stream()
            .filter(interaction -> interaction.getName().equalsIgnoreCase(interactionName))
            .findFirst();
    }

    @SuppressWarnings("unused")
    public boolean removeInteraction(@NotNull T interaction) {
        return interactions.remove(interaction);
    }
}
