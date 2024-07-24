package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButton extends IInteraction<ButtonInteractionEvent> {

    @Override
    default InteractionType getInteractionType() {
        return InteractionType.BUTTON;
    }
}
