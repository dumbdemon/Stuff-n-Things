package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IMessageContext extends ICommand {

    /**
     * The main handler for message context menus.
     *
     * @param event A {@link MessageContextInteractionEvent}.
     * @param guild The Guild the event was called on.
     * @throws Exception Any exception that could get thrown across all IMessageContexts.
     */
    void execute(@NotNull MessageContextInteractionEvent event, @NotNull Guild guild) throws Exception;
}
