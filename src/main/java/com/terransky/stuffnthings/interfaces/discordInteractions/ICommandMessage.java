package com.terransky.stuffnthings.interfaces.discordInteractions;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommandMessage extends ICommand {

    /**
     * The main handler for message context menus.
     *
     * @param event A {@link MessageContextInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown.
     */
    void execute(@NotNull MessageContextInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
