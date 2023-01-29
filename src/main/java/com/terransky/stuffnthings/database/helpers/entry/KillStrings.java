package com.terransky.stuffnthings.database.helpers.entry;

import com.terransky.stuffnthings.database.helpers.Property;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class KillStrings {

    @BsonProperty("guildId")
    private String idReference;
    @BsonProperty("killTargets")
    private List<String> killTargets;
    @BsonProperty("killRandoms")
    private List<String> killRandoms;

    public KillStrings() {
    }

    public KillStrings(String idReference) {
        this.idReference = idReference;
        this.killRandoms = new ArrayList<>();
        this.killTargets = new ArrayList<>();
    }

    @BsonProperty("guildId")
    public String getIdReference() {
        return idReference;
    }

    @BsonProperty("guildId")
    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    @BsonProperty("killTargets")
    public List<String> getKillTargets() {
        return killTargets;
    }

    @BsonProperty("killTargets")
    public void setKillTargets(List<String> killTargets) {
        this.killTargets = killTargets;
    }

    @BsonProperty("killRandoms")
    public List<String> getKillRandoms() {
        return killRandoms;
    }

    @BsonProperty("killRandoms")
    public void setKillRandoms(List<String> killRandoms) {
        this.killRandoms = killRandoms;
    }

    @BsonIgnore
    public Optional<Object> getProperty(@NotNull Property property) {
        switch (property) {
            case KILL_RANDOM -> {
                return Optional.ofNullable(killRandoms);
            }
            case KILL_TARGET -> {
                return Optional.ofNullable(killTargets);
            }
            default -> throw new IllegalArgumentException(String.format("%S is not a guild property.", property));
        }
    }

    @Override
    public String toString() {
        return "KillStrings{" +
            "killTargets=" + killTargets +
            ", killRandoms=" + killRandoms +
            '}';
    }
}
