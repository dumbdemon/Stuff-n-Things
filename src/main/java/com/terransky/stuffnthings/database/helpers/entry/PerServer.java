package com.terransky.stuffnthings.database.helpers.entry;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class PerServer implements Comparable<PerServer> {

    private String guildReference;
    private Long killAttempts;
    private Boolean killUnderTo;

    public PerServer() {
    }

    public PerServer(String guildReference) {
        this.guildReference = guildReference;
        this.killAttempts = 0L;
        this.killUnderTo = false;
    }

    public String getGuildReference() {
        return guildReference;
    }

    public void setGuildReference(String guildReference) {
        this.guildReference = guildReference;
    }

    public Long getKillAttempts() {
        return killAttempts;
    }

    public void setKillAttempts(Long killAttempts) {
        this.killAttempts = killAttempts;
    }

    public Boolean isKillUnderTo() {
        return killUnderTo;
    }

    public void setKillUnderTo(Boolean killUnderTo) {
        this.killUnderTo = killUnderTo;
    }

    @Override
    public int compareTo(@NotNull PerServer perServer) {
        return String.CASE_INSENSITIVE_ORDER.compare(getGuildReference(), perServer.getGuildReference());
    }

    @Override
    public String toString() {
        return "KillLock{" +
            "guildReference='" + guildReference + '\'' +
            ", killAttempts=" + killAttempts +
            ", killUnderTo=" + killUnderTo +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerServer perServer = (PerServer) o;
        return getGuildReference().equals(perServer.getGuildReference());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuildReference());
    }
}
