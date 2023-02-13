package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface ISelectMenuString extends IInteraction<StringSelectInteractionEvent> {

    @Override
    default Type getInteractionType() {
        return Type.SELECTION_STRING;
    }
}
