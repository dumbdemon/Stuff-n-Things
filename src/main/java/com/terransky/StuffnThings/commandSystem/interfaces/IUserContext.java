package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface IUserContext {
    //Menu name can have spaces
    String getName();

    CommandData commandData();

    void execute(@NotNull UserContextInteractionEvent event) throws Exception;
}
