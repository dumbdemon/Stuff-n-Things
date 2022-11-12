package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal extends IBaseDiscordElement {

    void execute(@NotNull ModalInteractionEvent event) throws Exception;
}
