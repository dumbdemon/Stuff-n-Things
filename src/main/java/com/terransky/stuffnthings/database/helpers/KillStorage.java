package com.terransky.stuffnthings.database.helpers;

public enum KillStorage {

    RANDOM(Property.KILL_RANDOM),
    TARGET(Property.KILL_TARGET);

    private final Property property;

    KillStorage(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
