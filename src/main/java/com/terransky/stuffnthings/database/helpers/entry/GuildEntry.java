package com.terransky.stuffnthings.database.helpers.entry;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class GuildEntry {

    @BsonProperty("guildId")
    private String idReference;
    @BsonProperty("killMaximum")
    private Long killMaximum;
    @BsonProperty("killTimeout")
    private Long killTimeout;

    public GuildEntry() {
    }

    public GuildEntry(String idReference) {
        this.idReference = idReference;
        this.killMaximum = 5L;
        this.killTimeout = TimeUnit.MINUTES.toMillis(10);
    }

    @BsonProperty("guildId")
    public String getIdReference() {
        return idReference;
    }

    @BsonProperty("guildId")
    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    @BsonProperty("killMaximum")
    public Long getKillMaximum() {
        return killMaximum;
    }

    @BsonProperty("killMaximum")
    public GuildEntry setKillMaximum(Long killMaximum) {
        this.killMaximum = killMaximum;
        return this;
    }

    @BsonProperty("killTimeout")
    public Long getKillTimeout() {
        return killTimeout;
    }

    @BsonProperty("killTimeout")
    public GuildEntry setKillTimeout(Long killTimeout) {
        this.killTimeout = killTimeout;
        return this;
    }

    @Override
    public String toString() {
        return "Guilds{" +
            "guildId=" + idReference +
            ", killsMaximum=" + killMaximum +
            ", killsTimeout=" + killTimeout +
            '}';
    }
}
