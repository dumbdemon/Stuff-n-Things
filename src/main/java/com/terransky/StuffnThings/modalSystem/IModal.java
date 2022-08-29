package com.terransky.StuffnThings.modalSystem;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal {
    String getModalID();

    void modalExecute(@NotNull ModalInteractionEvent event);
}
