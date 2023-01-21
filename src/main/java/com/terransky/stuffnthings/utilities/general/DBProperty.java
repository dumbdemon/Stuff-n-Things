package com.terransky.stuffnthings.utilities.general;

public enum DBProperty {
    KILLS_MAX("killcmd_max", Table.SERVER),
    KILLS_TIMEOUT("killcmd_timeout", Table.SERVER),
    KILL_ATTEMPTS("kill_attempts", Table.USER),
    KILL_TIMEOUT("kill_under_to", Table.USER);

    private final String id;
    private final Table table;

    DBProperty(String id, Table table) {
        this.id = id;
        this.table = table;
    }

    public String getId() {
        return id;
    }

    public Table getTable() {
        return table;
    }

    public enum Table {

        SERVER,
        USER;

        Table() {
        }
    }
}
