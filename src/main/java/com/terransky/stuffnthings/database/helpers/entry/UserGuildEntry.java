package com.terransky.stuffnthings.database.helpers.entry;

import com.terransky.stuffnthings.interfaces.Pojo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserGuildEntry extends PerServer implements Pojo {

    private long maxKills;
    private long serverTimeout;

    public UserGuildEntry(@NotNull PerServer perServer) {
        setKillAttempts(perServer.getKillAttempts());
        setKillEndTime(perServer.getKillEndTime());
        setKillUnderTo(perServer.getKillUnderTo());
    }

    public long getMaxKills() {
        return maxKills;
    }

    public UserGuildEntry setMaxKills(long maxKills) {
        this.maxKills = maxKills;
        return this;
    }

    public long getServerTimeout() {
        return serverTimeout;
    }

    public UserGuildEntry setServerTimeout(long serverTimeout) {
        this.serverTimeout = serverTimeout;
        return this;
    }

    public void resetAttempts() {
        setKillAttempts(0L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserGuildEntry entry = (UserGuildEntry) o;
        return getMaxKills() == entry.getMaxKills() && getServerTimeout() == entry.getServerTimeout();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMaxKills(), getServerTimeout());
    }
}
