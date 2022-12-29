package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IManager<T extends IInteraction> {

    final List<T> interactions = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(IManager.class);

    @SafeVarargs
    public IManager(@NotNull T... interactions) {
        for (T interaction : interactions) {
            addInteraction(interaction);
        }
    }

    /**
     * Add an {@link IInteraction} to the list.
     *
     * @param interaction An {@link IInteraction}
     * @throws IllegalArgumentException Either the {@link IInteraction} already exists in the index or the type is either
     *                                  {@link InteractionType#COMMAND_SLASH}, {@link InteractionType#COMMAND_MESSAGE}, or
     *                                  {@link InteractionType#COMMAND_USER}.
     */
    void addInteraction(@NotNull T interaction) {
        boolean interactionFound = interactions.stream().anyMatch(it -> it.getName().equalsIgnoreCase(interaction.getName()));
        InteractionType type = interaction.getInteractionType();

        if (type.hasDedicatedManager())
            throw new IllegalArgumentException("Please use the appropriate manager for this type.");

        if (interactionFound) throw new IllegalArgumentException("An interaction with that name already exists");

        interactions.add(interaction);
    }

    /**
     * Get an {@link IInteraction} from the list.
     *
     * @param search The name of a command to look for
     * @return Get an {@link Optional} of an {@link IInteraction}
     */
    public Optional<T> getInteraction(@NotNull String search) {
        for (T interaction : interactions) {
            if (interaction.getName().equalsIgnoreCase(search)) {
                return Optional.of(interaction);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the effective amount of {@link IInteraction}.
     *
     * @param interactions A {@link List} of {@link IInteraction}s.
     * @param type         An {@link InteractionType}.
     * @return A {@link List} of {@link IInteraction}s that does not exceed their maximum count.
     */
    @NotNull
    List<T> getEffectiveCounts(@NotNull List<T> interactions, @NotNull InteractionType type) {
        int max = type.getMaximum();
        log.info("Checking quantity of %ss against maximum of %d...".formatted(type.getName(), max));
        if (interactions.size() > max) {
            log.warn("There are too many %ss (there's %d)! Truncating to %d..."
                .formatted(type.getName(), interactions.size(), max));
            return interactions.subList(0, max);
        }
        return interactions;
    }
}
