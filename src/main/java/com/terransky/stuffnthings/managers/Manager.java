package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Manager<T extends IInteraction> {

    private final List<T> interactions = new ArrayList<>();

    @SafeVarargs
    public Manager(@NotNull T... interactions) {
        for (T interaction : interactions) {
            addInteraction(interaction);
        }
    }

    public void addInteraction(T interaction) {
        boolean interactionFound = interactions.stream().anyMatch(it -> it.getName().equalsIgnoreCase(interaction.getName()));

        if (interactionFound) throw new IllegalArgumentException("An interaction with that name already exists");

        interactions.add(interaction);
    }

    public Optional<T> getInteraction(@NotNull String search) {
        for (T interaction : interactions) {
            if (interaction.getName().equalsIgnoreCase(search)) {
                return Optional.of(interaction);
            }
        }

        return Optional.empty();
    }
}
