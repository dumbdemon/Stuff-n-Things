package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.utilities.EventBlob;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommandUser extends ICommand {

    /**
     * The main handler for user context menus.
     *
     * @param event A {@link UserContextInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all IUserContexts.
     */
    void execute(@NotNull UserContextInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
