package com.terransky.stuffnthings.database.helpers.entry;

import java.util.Objects;

public class UserGuildEntry {

    private final KillLock killLock;
    private long maxKills;
    private long timeout;

    public UserGuildEntry(KillLock killLock) {
        this.killLock = killLock;
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
        return killLock.getKillAttempts();
    }

    public boolean isUnderTimeout() {
        return killLock.isKillUnderTo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGuildEntry entry = (UserGuildEntry) o;
        return getMaxKills() == entry.getMaxKills() && getTimeout() == entry.getTimeout() && killLock.equals(entry.killLock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(killLock, getMaxKills(), getTimeout());
    }
}
