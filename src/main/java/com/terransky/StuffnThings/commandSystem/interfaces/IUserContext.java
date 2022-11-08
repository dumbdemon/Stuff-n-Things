package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IUserContext extends IBaseCommand {

    void execute(@NotNull UserContextInteractionEvent event) throws Exception;
}
