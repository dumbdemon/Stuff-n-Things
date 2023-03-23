package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class IManager<T extends IInteraction<?>> {

    protected final List<T> interactions = new ArrayList<>();
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
     * @throws IllegalArgumentException Thrown if the {@link IInteraction} type has a dedicated manager.
     */
    public void addInteraction(@NotNull T interaction) {
        if (interaction.getInteractionType().hasDedicatedManager())
            throw new IllegalArgumentException("Please use the appropriate manager for this type.");
        noTypeCheckAddInteraction(interaction);
    }

    /**
     * Add interactions to this {@link IManager}
     *
     * @param interactions A List of {@link IInteraction IInteractions}
     */
    public void addInteractions(@NotNull Collection<T> interactions) {
        interactions.forEach(this::addInteraction);
    }

    /**
     * Add an {@link IInteraction} to the index without type checking.<br>
     * Please use {@link #addInteraction(IInteraction)} when adding normally.
     *
     * @param interaction An {@link IInteraction}
     */
    void noTypeCheckAddInteraction(@NotNull T interaction) {
        boolean interactionFound = interactions.stream().anyMatch(it -> it.getName().equalsIgnoreCase(interaction.getName()));

        if (interactionFound)
            log.warn("An interaction with that name already exists");
        else
            interactions.add(interaction);
    }

    /**
     * Get an {@link IInteraction} from the list.
     *
     * @param search The name of a command to look for
     * @return Get an {@link Optional} of an {@link IInteraction}
     */
    public Optional<T> getInteraction(@NotNull String search) {
        return interactions.stream()
            .filter(interaction -> interaction.getName().equalsIgnoreCase(search))
            .findFirst();
    }

    /**
     * Get the effective amount of {@link IInteraction}.
     *
     * @param interactions A {@link List} of {@link IInteraction}s.
     * @param type         An {@link IInteraction.Type}.
     * @return A {@link List} of {@link IInteraction}s that does not exceed their maximum count.
     */
    @NotNull
    List<T> getEffectiveCounts(@NotNull List<T> interactions, @NotNull IInteraction.Type type) {
        int max = type.getMaximum();
        log.info("Checking quantity of {}s against maximum of {}...", type.getName(), max);
        if (interactions.size() > max) {
            log.warn("There are too many {}s (there's {})! Truncating to {}...", type.getName(), interactions.size(), max);
            return interactions.subList(0, max);
        }
        return interactions;
    }
}
