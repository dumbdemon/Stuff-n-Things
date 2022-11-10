package com.terransky.StuffnThings.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IUserContext extends ICommand {

    void execute(@NotNull UserContextInteractionEvent event) throws Exception;
}
