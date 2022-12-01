package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IModal extends IInteractionElement {

    /**
     * The main handler for modals.
     *
     * @param event A {@link ModalInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all IModals.
     */
    void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
