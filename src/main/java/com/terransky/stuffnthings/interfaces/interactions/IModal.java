package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface IModal extends IInteraction<ModalInteractionEvent> {

    @Override
    default Type getInteractionType() {
        return Type.MODAL;
    }
}
