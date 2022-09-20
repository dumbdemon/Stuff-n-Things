package com.terransky.StuffnThings.modalSystem;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal {
    String getID();

    void execute(@NotNull ModalInteractionEvent event);
}
