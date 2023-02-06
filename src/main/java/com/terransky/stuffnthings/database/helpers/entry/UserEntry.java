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
    private List<PerServer> perServers;

    /**
     * No-args constructor for Mongo Driver
     */
    public UserEntry() {
    }

    /**
     * Initializes a new UserEntry for new user
     *
     * @param idReference A {@link net.dv8tion.jda.api.entities.User user}'s id
     */
    public UserEntry(String idReference) {
        this.idReference = idReference;
        this.perServers = new ArrayList<>();
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
    public List<PerServer> getKillLocks() {
        return perServers;
    }

    @BsonProperty("killLocks")
    public void setKillLocks(List<PerServer> perServers) {
        this.perServers = perServers;
    }

    @BsonIgnore
    public Optional<Object> getProperty(@NotNull Property property, String idReference) {
        Predicate<PerServer> isItThisOne = lock -> lock.getGuildReference().equals(idReference);
        switch (property) {
            case KILL_TIMEOUT -> {
                return perServers.stream().filter(isItThisOne).findFirst()
                    .map(PerServer::isKillUnderTo);
            }
            case KILL_ATTEMPTS -> {
                return perServers.stream().filter(isItThisOne).findFirst()
                    .map(PerServer::getKillAttempts);
            }
            case KILL_LOCK ->
                throw new IllegalArgumentException(String.format("%S is not intended to be called.", property));
            default -> throw new IllegalArgumentException(String.format("%S is not a guild property.", property));
        }
    }

    @Override
    public String toString() {
        return "SNTUser{" +
            "killLocks=" + perServers +
            ", idReference='" + idReference + '\'' +
            '}';
    }
}
