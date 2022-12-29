package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public interface ICommandUser extends ICommand {

    @Override
    default CommandData getCommandData() {
        return Commands.user(getName());
    }

    /**
     * The main handler for user context menus.
     *
     * @param event A {@link UserContextInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown.
     */
    void execute(@NotNull UserContextInteractionEvent event, @NotNull EventBlob blob) throws Exception;

    @Override
    default InteractionType getInteractionType() {
        return InteractionType.COMMAND_USER;
    }
}
