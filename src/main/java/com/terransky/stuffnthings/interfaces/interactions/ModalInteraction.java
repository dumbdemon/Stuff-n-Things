package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.modals.Modal;

public abstract class ModalInteraction implements IInteraction.IModal {

    private final String id;
    private final String title;
    private boolean deferReply = false;
    private boolean ephemeral = false;

    protected ModalInteraction(String id, String title) {
        this.id = id;
        this.title = title;
    }

    protected Modal.Builder getBuilder() {
        return Modal.create(id, title);
    }

    public abstract Modal getContructedModal();

    @Override
    public void setDeferReply() {
        this.deferReply = true;
    }

    @Override
    public boolean deferReply() {
        return this.deferReply;
    }

    @Override
    public void setEphemeral() {
        this.ephemeral = true;
    }

    @Override
    public boolean isEphemeral() {
        return this.ephemeral;
    }

    @Override
    public String getName() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
