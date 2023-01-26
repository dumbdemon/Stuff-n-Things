package com.terransky.stuffnthings.database.helpers.entry;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String toString() {
        return "KillStrings{" +
            "killTargets=" + killTargets +
            ", killRandoms=" + killRandoms +
            '}';
    }
}
