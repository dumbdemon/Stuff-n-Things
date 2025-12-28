package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class SelectMenuStringInteraction implements IInteraction.ISelectMenuString {

    private final String id;

    protected SelectMenuStringInteraction(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public abstract void execute(@NotNull StringSelectInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException;
}
