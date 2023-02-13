package com.terransky.stuffnthings.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public interface ICommandUser extends ICommand<UserContextInteractionEvent> {

    @Override
    default CommandData getCommandData() {
        return Commands.user(getName());
    }

    @Override
    default Type getInteractionType() {
        return Type.COMMAND_USER;
    }
}
