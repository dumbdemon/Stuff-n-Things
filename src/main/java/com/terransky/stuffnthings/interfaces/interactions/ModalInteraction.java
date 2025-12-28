package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class ModalInteraction implements IInteraction.IModal {

    private final String id;
    private final String title;

    protected ModalInteraction(String id, String title) {
        this.id = id;
        this.title = title;
    }

    protected Modal.Builder getBuilder() {
        return Modal.create(id, title);
    }

    public abstract Modal getContructedModal();

    public String getTitle() {
        return title;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public abstract void execute(@NotNull ModalInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException;
}
