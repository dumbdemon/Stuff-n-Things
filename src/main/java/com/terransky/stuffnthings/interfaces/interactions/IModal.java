package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

public interface IModal extends IInteraction<ModalInteractionEvent> {

    Modal getConstructedModal();

    @Override
    default Type getInteractionType() {
        return Type.MODAL;
    }
}
