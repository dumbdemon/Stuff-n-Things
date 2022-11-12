package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IButton extends IBaseDiscordElement {

    void execute(@NotNull ButtonInteractionEvent event) throws Exception;
}
