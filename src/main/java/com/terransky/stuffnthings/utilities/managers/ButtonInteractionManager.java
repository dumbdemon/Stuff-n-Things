package com.terransky.stuffnthings.utilities.managers;

import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.utilities.general.InteractionType;

import java.util.List;
import java.util.Optional;

public class ButtonInteractionManager extends InteractionManager<ButtonInteraction> {

    public ButtonInteractionManager() {
        super(InteractionType.BUTTON);
    }

    @Override
    public Optional<ButtonInteraction> getInteraction(String interactionName) {
        List<ButtonInteraction> interactions;

        interactions = this.interactions.stream()
            .filter(buttonInteraction -> buttonInteraction.followsPattern(interactionName))
            .toList();

        return interactions.isEmpty() ? super.getInteraction(interactionName) : interactions.stream().findFirst();
    }
}
