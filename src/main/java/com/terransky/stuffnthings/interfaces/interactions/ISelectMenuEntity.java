package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

public interface ISelectMenuEntity extends IInteraction<EntitySelectInteractionEvent> {

    @Override
    default Type getInteractionType() {
        return Type.SELECTION_ENTITY;
    }
}
