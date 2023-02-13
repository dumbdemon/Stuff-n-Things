package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButton extends IInteraction<ButtonInteractionEvent> {

    @Override
    default Type getInteractionType() {
        return Type.BUTTON;
    }
}
