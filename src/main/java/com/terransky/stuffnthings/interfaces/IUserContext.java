package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IUserContext extends ICommand {

    /**
     * The main handler for user context menus.
     *
     * @param event A {@link UserContextInteractionEvent}.
     * @throws Exception Any exception that could get thrown across all IUserContexts.
     */
    void execute(@NotNull UserContextInteractionEvent event) throws Exception;
}
