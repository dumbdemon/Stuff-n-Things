package com.terransky.stuffnthings.utilities;

public enum SlashModule {

    ADMIN("Admin", 0),
    DEVS("Developer", 1),
    FUN("Fun", 2),
    GENERAL("General", 3),
    MATHS("Math", 4),
    MTG("Magic: the Gathering", 5);

    private final String name;
    private final int id;

    SlashModule(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
