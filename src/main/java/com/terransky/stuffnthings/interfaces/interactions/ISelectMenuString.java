package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface ISelectMenuString extends IInteraction<StringSelectInteractionEvent> {

    @Override
    default InteractionType getInteractionType() {
        return InteractionType.SELECTION_STRING;
    }
}
