package com.terransky.stuffnthings.utilities.general;

import net.dv8tion.jda.api.interactions.commands.build.Commands;

public enum IInteractionType {

    UNKNOWN(-1, "UNKNOWN", 0), //For future interactions
    COMMAND_SLASH(0, "Slash Command", Commands.MAX_SLASH_COMMANDS, true),
    COMMAND_USER(1, "User Context Menu", Commands.MAX_USER_COMMANDS, true),
    COMMAND_MESSAGE(2, "Message Context Menu", Commands.MAX_MESSAGE_COMMANDS, true),
    BUTTON(3, "Button"),
    MODAL(4, "Modal"),
    SELECTION_STRING(5, "Selection Menu"),
    SELECTION_ENTITY(6, "Entity Selection Menu");

    private final int id;
    private final String name;
    private final int maximum;
    private final boolean hasDedicatedManager;

    IInteractionType(int id, String name) {
        this(id, name, Integer.MAX_VALUE, false);
    }

    IInteractionType(int id, String name, int maximum) {
        this(id, name, maximum, false);
    }

    IInteractionType(int id, String name, int maximum, boolean hasDedicatedManager) {
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
