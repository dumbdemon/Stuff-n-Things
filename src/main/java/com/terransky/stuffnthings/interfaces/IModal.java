package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal extends IBaseBotElement {

    /**
     * The main handler for modals.
     *
     * @param event A {@link ModalInteractionEvent}.
     * @throws Exception Any exception that could get thrown across all IModals.
     */
    void execute(@NotNull ModalInteractionEvent event) throws Exception;
}
