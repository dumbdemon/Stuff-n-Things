package com.terransky.StuffnThings.buttonSystem;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IButton {
    String getID();

    void execute(@NotNull ButtonInteractionEvent event) throws Exception;
}
