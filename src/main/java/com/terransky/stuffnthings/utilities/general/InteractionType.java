package com.terransky.stuffnthings.utilities.general;

import net.dv8tion.jda.api.interactions.commands.build.Commands;

public enum InteractionType {

    UNKNOWN(-1, "UNKNOWN", 0), //For future interactions
    COMMAND_SLASH(0, "Slash Command", Commands.MAX_SLASH_COMMANDS, true),
    BUTTON(1, "Button"),
    MODAL(2, "Modal"),
    SELECTION_STRING(3, "Selection Menu"),
    SELECTION_ENTITY(4, "Entity Selection Menu"),
    COMMAND_CONTEXT_MESSAGE(5, "Message Context Menu", Commands.MAX_MESSAGE_COMMANDS, true),
    COMMAND_CONTEXT_USER(6, "User Context Menu", Commands.MAX_USER_COMMANDS, true);

    private final int id;
    private final String name;
    private final int maximum;
    private final boolean hasDedicatedManager;

    InteractionType(int id, String name) {
        this(id, name, Integer.MAX_VALUE, false);
    }

    InteractionType(int id, String name, int maximum) {
        this(id, name, maximum, false);
    }

    InteractionType(int id, String name, int maximum, boolean hasDedicatedManager) {
        this.id = id;
        this.name = name;
        this.maximum = maximum;
        this.hasDedicatedManager = hasDedicatedManager;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean hasDedicatedManager() {
        return hasDedicatedManager;
    }
}
