package com.terransky.stuffnthings.utilities.general;

@SuppressWarnings("unused")
public enum Interactions {

    UNKNOWN(-1, "UNKNOWN"), //For future interactions
    COMMAND_SLASH(0, "Slash Command"),
    BUTTON(1, "Button", true),
    MODAL(2, "Modal", true),
    SELECTION_STRING(3, "Selection Menu", true),
    SELECTION_ENTITY(4, "Entity Selection Menu", true),
    COMMAND_MESSAGE(5, "Message Context Menu", true),
    COMMAND_USER(6, "User Context Menu", true);

    private final int id;
    private final String name;
    private final boolean gui;

    Interactions(int id, String name) {
        this(id, name, false);
    }

    Interactions(int id, String name, boolean gui) {
        this.id = id;
        this.name = name;
        this.gui = gui;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isGui() {
        return gui;
    }
}
