package com.terransky.stuffnthings.database.helpers.entry;

import java.time.OffsetDateTime;
import java.util.Objects;

public class UserGuildEntry {

    private final PerServer perServer;
    private long maxKills;
    private long timeout;
    private OffsetDateTime endTime;

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
        return perServer.getKillUnderTo();
    }

    public void resetAttempts() {
        perServer.setKillAttempts(0L);
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public UserGuildEntry setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
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
