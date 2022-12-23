package com.terransky.stuffnthings.utilities.general;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unused")
public enum Interactions {

    UNKNOWN(-1, "UNKNOWN"), //For future interactions
    SLASH_COMMAND(0, "Slash Command"),
    BUTTON(1, "Button", true),
    MODAL(2, "Modal", true),
    SELECTION_STRING(3, "Selection Menu", true),
    SELECTION_ENTITY(4, "Entity Selection Menu", true),
    CONTEXT_MESSAGE(5, "Message Context Menu", true),
    CONTEXT_USER(6, "User Context Menu", true);

    private final int id;
    private final String type;
    private final boolean gui;

    Interactions(int id, String type) {
        this(id, type, false);
    }

    Interactions(int id, String type, boolean gui) {
        this.id = id;
        this.type = type;
        this.gui = gui;
    }

    @NotNull
    public static EnumSet<Interactions> getAllInteractions() {
        EnumSet<Interactions> enumSet = EnumSet.allOf(Interactions.class);
        enumSet.remove(Interactions.UNKNOWN);
        return enumSet;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isGui() {
        return gui;
    }
}
