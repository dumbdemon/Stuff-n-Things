package com.terransky.stuffnthings.database.helpers.entry;

import com.terransky.stuffnthings.database.helpers.Property;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    @BsonIgnore
    public Optional<Object> getProperty(@NotNull Property property, String idReference) {
        Predicate<KillLock> isItThisOne = lock -> lock.getGuildReference().equals(idReference);
        switch (property) {
            case KILL_TIMEOUT -> {
                return killLocks.stream().filter(isItThisOne).findFirst()
                    .map(KillLock::isKillUnderTo);
            }
            case KILL_ATTEMPTS -> {
                return killLocks.stream().filter(isItThisOne).findFirst()
                    .map(KillLock::getKillAttempts);
            }
            case KILL_LOCK ->
                throw new IllegalArgumentException(String.format("%S is not intended to be called.", property));
            default -> throw new IllegalArgumentException(String.format("%S is not a guild property.", property));
        }
    }

    @Override
    public String toString() {
        return "SNTUser{" +
            "killLocks=" + killLocks +
            ", idReference='" + idReference + '\'' +
            '}';
    }
}
