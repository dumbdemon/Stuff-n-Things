package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal extends IBaseBotElement {

    /**
     * The main handler for modals.
     *
     * @param event A {@link ModalInteractionEvent}.
     * @param guild The Guild the event was called on.
     * @throws Exception Any exception that could get thrown across all IModals.
     */
    void execute(@NotNull ModalInteractionEvent event, @NotNull Guild guild) throws Exception;
}
