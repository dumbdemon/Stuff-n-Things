package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IMessageContext extends IBaseCommand {

    void execute(@NotNull MessageContextInteractionEvent event) throws Exception;
}
