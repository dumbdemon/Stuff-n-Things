package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;

public abstract class SelectMenuStringInteraction implements IInteraction.ISelectMenuString {

    private final String id;

    protected SelectMenuStringInteraction(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }
}
