package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ICommandMessage extends ICommand {

    @Override
    default CommandData getCommandData() {
        return Commands.message(getName());
    }

    /**
     * The main handler for message context menus.
     *
     * @param event A {@link MessageContextInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws RuntimeException Any exception thrown that could prevent operation.
     * @throws IOException      Potentially could be thrown during network operations
     */
    void execute(@NotNull MessageContextInteractionEvent event, @NotNull EventBlob blob) throws RuntimeException, IOException;

    @Override
    default Type getInteractionType() {
        return Type.COMMAND_MESSAGE;
    }
}
