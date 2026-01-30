package com.terransky.stuffnthings.utilities.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;

public class InteractionManager<T extends IInteraction<?>> {
    protected HashSet<T> interactions = new HashSet<>();
    private final InteractionType interactionType;

    protected InteractionManager(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    protected final void addInteraction(@NotNull T interaction) {
        if (interaction.getInteractionType().hasDedicatedManager() && interactionType != interaction.getInteractionType()) {
            LoggerFactory.getLogger(getClass())
                .error("Interaction has dedicated manager. Please us that manager instead", new IllegalArgumentException());
        } else interactions.add(interaction);
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
