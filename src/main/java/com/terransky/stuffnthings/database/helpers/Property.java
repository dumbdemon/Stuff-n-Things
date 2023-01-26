package com.terransky.stuffnthings.database.helpers;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import org.jetbrains.annotations.NotNull;

public enum Property {

    ID_REFERENCE(Table.ROOT),
    KILLS_MAX("killMaximum", Table.GUILD),
    KILLS_TIMEOUT("killTimeout", Table.GUILD),
    KILL_LOCK("killLocks", Table.USER),
    KILL_ATTEMPTS("killAttempts", Table.USER),
    KILL_TIMEOUT("killUnderTo", Table.USER),
    KILL_TARGET("killTargets", Table.KILL),
    KILL_RANDOM("killRandoms", Table.KILL),
    ;

    private final String propertyName;
    private final Table table;

    Property(Table table) {
        this(null, table);
    }

    Property(String propertyName, Table table) {
        this.propertyName = propertyName;
        this.table = table;
    }

    public String getPropertyName() {
        if (this == ID_REFERENCE)
            throw new IllegalArgumentException(String.format("Called for %S but didn't include a %S", this, Table.class.getName()));
        return propertyName;
    }

    public String getPropertyName(@NotNull Table table) {
        switch (table) {
            case USER -> {
                return "userId";
            }
            case GUILD, KILL -> {
                return "guildId";
            }
            default -> {
                return propertyName;
            }
        }
    }

    public Table getTable() {
        return table;
    }

    public enum Table {

        ROOT,
        GUILD,
        USER,
        KILL,
        ;

        public String getTarget(EventBlob blob, Property property) {
            switch (this) {
                case USER -> {
                    return blob.getMemberId();
                }
                case GUILD -> {
                    return blob.getGuildId();
                }
                case KILL -> {
                    return "0";
                }
                default ->
                    throw new IllegalArgumentException(String.format("The property %s is used for identification purposes only", property));
            }
        }
    }
}
