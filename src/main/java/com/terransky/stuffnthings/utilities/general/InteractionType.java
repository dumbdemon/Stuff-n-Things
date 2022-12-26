package com.terransky.stuffnthings.utilities.general;

public enum InteractionType {

    UNKNOWN(-1, "UNKNOWN"), //For future interactions
    COMMAND_SLASH(0, "Slash Command", true),
    BUTTON(1, "Button"),
    MODAL(2, "Modal"),
    SELECTION_STRING(3, "Selection Menu"),
    SELECTION_ENTITY(4, "Entity Selection Menu"),
    COMMAND_CONTEXT_MESSAGE(5, "Message Context Menu", true),
    COMMAND_CONTEXT_USER(6, "User Context Menu", true);

    private final int id;
    private final String name;
    private final boolean hasDedicatedManager;

    InteractionType(int id, String name) {
        this(id, name, false);
    }

    InteractionType(int id, String name, boolean hasDedicatedManager) {
        this.id = id;
        this.name = name;
        this.hasDedicatedManager = hasDedicatedManager;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasDedicatedManager() {
        return hasDedicatedManager;
    }
}
