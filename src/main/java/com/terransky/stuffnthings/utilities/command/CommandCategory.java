package com.terransky.stuffnthings.utilities.command;

public enum CommandCategory {

    TEST("Test"),
    ADMIN("Admin"),
    DEVS("Developer"),
    FUN("Fun"),
    GENERAL("General"),
    MATHS("Math"),
    MTG("Magic: the Gathering");

    private final String name;

    CommandCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
