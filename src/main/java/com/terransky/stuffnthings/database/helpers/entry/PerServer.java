package com.terransky.stuffnthings.database.helpers.entry;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@SuppressWarnings("unused")
public class PerServer implements Comparable<PerServer> {

    @BsonIgnore
    private final OffsetDateTime DEFAULT_DT = OffsetDateTime.parse("2023-02-11T14:27Z");
    private String guildReference;
    private Long killAttempts;
    private Boolean killUnderTo;
    private String killEndTime;

    public PerServer() {
    }

    public PerServer(String guildReference) {
        this.guildReference = guildReference;
        this.killAttempts = 0L;
        this.killEndTime = DEFAULT_DT.format(DateTimeFormatter.ISO_INSTANT);
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

    public Boolean getKillUnderTo() {
        return killUnderTo;
    }

    public void setKillUnderTo(Boolean killUnderTo) {
        this.killUnderTo = killUnderTo;
    }

    public String getKillEndTime() {
        if (killEndTime == null)
            return DEFAULT_DT.format(DateTimeFormatter.ISO_INSTANT);
        return killEndTime;
    }

    public void setKillEndTime(String killEndTime) {
        this.killEndTime = killEndTime;
    }

    @BsonIgnore
    public OffsetDateTime getKillEndTimeAsDate() {
        if (killEndTime == null)
            return DEFAULT_DT;
        return OffsetDateTime.parse(killEndTime);
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
        return getGuildReference().equals(perServer.getGuildReference()) &&
            getKillAttempts().equals(perServer.getKillAttempts()) &&
            getKillUnderTo().equals(perServer.getKillUnderTo()) &&
            getKillEndTime().equals(perServer.getKillEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuildReference(), getKillAttempts(), getKillUnderTo(), getKillEndTime());
    }
}
