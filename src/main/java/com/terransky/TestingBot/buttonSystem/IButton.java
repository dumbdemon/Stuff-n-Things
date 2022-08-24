package com.terransky.TestingBot.buttonSystem;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IButton {
    String getButtonID();

    void buttonExecute(@NotNull ButtonInteractionEvent event);
}
