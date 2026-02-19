package com.terransky.stuffnthings.interfaces.interactions;

import net.dv8tion.jda.api.modals.Modal;

public abstract class ModalObjectInteraction<T> extends ModalInteraction {

    protected ModalObjectInteraction(String id, String title) {
        super(id, title);
    }

    @Override
    public Modal getContructedModal() {
        throw new IllegalArgumentException("No object provided.");
    }

    public abstract Modal getContructedModal(T t);
}
