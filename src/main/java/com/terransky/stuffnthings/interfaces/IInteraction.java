package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IInteraction<T extends GenericInteractionCreateEvent> extends Comparable<IInteraction<T>> {

    /**
     * The name or ID reference of this bot element.
     *
     * @return A {@link String} of this bot element.
     */
    String getName();

    /**
     * Get the interaction type.
     *
     * @return An {@link InteractionType}.
     */
    default InteractionType getInteractionType() {
        return InteractionType.UNKNOWN;
    }

    /**
     * The main handler for all interactions
     *
     * @param event A Discord {@link GenericInteractionCreateEvent event}
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws FailedInteractionException Any exception thrown that could prevent operation.
     * @throws IOException                Potentially could be thrown during network operations
     * @throws ExecutionException         Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} could not be completed
     * @throws InterruptedException       Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} call gets interrupted.
     */
    void execute(@NotNull T event, EventBlob blob)
        throws FailedInteractionException, IOException, ExecutionException, InterruptedException;

    @Override
    default int compareTo(@NotNull IInteraction iInteraction) {
        return String.CASE_INSENSITIVE_ORDER.compare(getName(), iInteraction.getName()) |
            getInteractionType().compareTo(iInteraction.getInteractionType());
    }
}
