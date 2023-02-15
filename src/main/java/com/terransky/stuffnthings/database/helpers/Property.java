package com.terransky.stuffnthings.database.helpers;

import com.terransky.stuffnthings.database.helpers.entry.PerServer;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Formatter;
import org.jetbrains.annotations.NotNull;

public enum Property {

    ID_REFERENCE(Table.ROOT),
    KILLS_MAX("killMaximum", Table.GUILD),
    KILLS_TIMEOUT("killTimeout", Table.GUILD),
    REPORT_WEBHOOK("reportWebhook", Table.GUILD),
    REPORT_RESPONSE("reportResponse", Table.GUILD),
    JOKE_FLAGS("joke_flags", Table.GUILD),
    PER_SERVER("killLocks", Table.USER),
    KILL_ATTEMPTS(Table.USER),
    KILL_TIMEOUT(Table.USER),
    KILL_END_DATE(Table.USER),
    LAST_BINGO(Table.USER),
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
            throw new IllegalArgumentException(String.format("Called for %S but didn't include a %S", this, Formatter.getNameOfClass(Table.class)));
        if (propertyName == null)
            throw new IllegalArgumentException(String.format("Property %S is a %s property; therefore, it's property name is not required.", this,
                Formatter.getNameOfClass(PerServer.class)));
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

        public String getTarget(EventBlob blob) {
            switch (this) {
                case USER -> {
                    return blob.getMemberId();
                }
                case GUILD -> {
                    return blob.getGuildId();
                }
                case KILL -> {
                    if (blob.getGuild() == null && blob.getMember() == null)
                        return "1";
                    return "0";
                }
                default ->
                    throw new IllegalArgumentException(String.format("%S properties are intended to be used for identification purposes only", this));
            }
        }
    }

    public enum Games {
        BINGO
    }
}
