package com.terransky.stuffnthings.database.helpers.entry;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class KillLock implements Comparable<KillLock> {

    private String guildReference;
    private Long killAttempts;
    private Boolean killUnderTo;

    public KillLock() {
    }

    public KillLock(String guildReference) {
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
    public int compareTo(@NotNull KillLock killLock) {
        return String.CASE_INSENSITIVE_ORDER.compare(getGuildReference(), killLock.getGuildReference());
    }

    @Override
    public String toString() {
        return "KillLock{" +
            "guildReference='" + guildReference + '\'' +
            ", killAttempts=" + killAttempts +
            ", killUnderTo=" + killUnderTo +
            '}';
    }
}
