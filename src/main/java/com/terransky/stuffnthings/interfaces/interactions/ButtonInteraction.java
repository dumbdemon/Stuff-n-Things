package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class ButtonInteraction implements IInteraction.IButton {

    private final String name;

    protected ButtonInteraction(String name) {
        this.name = name;
    }

    public Button getButton(ButtonStyle style, String label) {
        return new ButtonImpl(getName(), label, style, false, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract void execute(@NotNull ButtonInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException;
}
