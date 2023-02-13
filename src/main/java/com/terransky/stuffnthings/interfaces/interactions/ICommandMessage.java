package com.terransky.stuffnthings.interfaces.interactions;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public interface ICommandMessage extends ICommand<MessageContextInteractionEvent> {

    @Override
    default CommandData getCommandData() {
        return Commands.message(getName());
    }

    @Override
    default Type getInteractionType() {
        return Type.COMMAND_MESSAGE;
    }
}
