package com.terransky.stuffnthings.database.helpers.entry;

import java.util.Objects;

public class UserGuildEntry {

    private final PerServer perServer;
    private long maxKills;
    private long timeout;

    public UserGuildEntry(PerServer perServer) {
        this.perServer = perServer;
    }

    public PerServer getPerServer() {
        return perServer;
    }

    public long getMaxKills() {
        return maxKills;
    }

    public UserGuildEntry setMaxKills(long maxKills) {
        this.maxKills = maxKills;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public UserGuildEntry setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public long getKillAttempts() {
        return perServer.getKillAttempts();
    }

    public boolean isUnderTimeout() {
        return perServer.isKillUnderTo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGuildEntry entry = (UserGuildEntry) o;
        return getMaxKills() == entry.getMaxKills() && getTimeout() == entry.getTimeout() && perServer.equals(entry.getPerServer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPerServer(), getMaxKills(), getTimeout());
    }
}
