package com.terransky.StuffnThings.interfaces;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IMessageContext extends ICommand {

    void execute(@NotNull MessageContextInteractionEvent event) throws Exception;
}
