package com.terransky.stuffnthings.database.helpers.entry;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserEntry {

    @BsonProperty("userId")
    private String idReference;
    @BsonProperty("killLocks")
    private List<KillLock> killLocks;

    public UserEntry() {
    }

    public UserEntry(String idReference) {
        this.idReference = idReference;
        this.killLocks = new ArrayList<>();
    }

    @BsonProperty("userId")
    public String getIdReference() {
        return idReference;
    }

    @BsonProperty("userId")
    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    @BsonProperty("killLocks")
    public List<KillLock> getKillLocks() {
        return killLocks;
    }

    @BsonProperty("killLocks")
    public void setKillLocks(List<KillLock> killLocks) {
        this.killLocks = killLocks;
    }

    @Override
    public String toString() {
        return "SNTUser{" +
            "killLocks=" + killLocks +
            ", idReference='" + idReference + '\'' +
            '}';
    }
}
