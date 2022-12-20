package com.terransky.stuffnthings.utilities;

@SuppressWarnings("unused")
public enum SelectMenuType {

    UNKNOWN(-1, "UNKNOWN"),
    Strings(0, "String"),
    Entities(1, "Entity");

    private final int id;
    private final String name;

    SelectMenuType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
