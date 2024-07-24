package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;

public interface IModal extends IInteraction<ModalInteractionEvent> {

    Modal getConstructedModal();

    @Override
    default InteractionType getInteractionType() {
        return InteractionType.MODAL;
    }
}
