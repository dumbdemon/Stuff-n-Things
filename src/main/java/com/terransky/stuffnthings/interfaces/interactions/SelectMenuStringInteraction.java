package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;

public abstract class SelectMenuStringInteraction implements IInteraction.ISelectMenuString {

    private final String id;
    private boolean deferReply = false;
    private boolean ephemeral = false;

    protected SelectMenuStringInteraction(String id) {
        this.id = id;
    }

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
}
