package com.terransky.stuffnthings.database.helpers.entry;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.utilities.command.Formatter;
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
    @BsonProperty("isBotBan")
    private Boolean botBanned;
    @BsonProperty("isSupportGuildBan")
    private Boolean supportGuildBan;

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

    @BsonIgnore
    public static UserEntry asUserEntry(Object entry) {
        if (entry instanceof UserEntry userEntry)
            return userEntry;
        throw new IllegalArgumentException(String.format("Object is not %s. Is it %s?",
            Formatter.getNameOfClass(UserEntry.class), Formatter.getNameOfClass(GuildEntry.class)));
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
    public List<PerServer> getPerServers() {
        return perServers;
    }

    @BsonProperty("killLocks")
    public void setPerServers(List<PerServer> perServers) {
        this.perServers = List.copyOf(perServers);
    }

    @BsonProperty("isBotBan")
    public Boolean getBotBanned() {
        return botBanned;
    }

    @BsonProperty("isBotBan")
    public void setBotBanned(Boolean botBanned) {
        this.botBanned = botBanned;
    }

    @BsonProperty("isSupportGuildBan")
    public Boolean getSupportGuildBan() {
        return supportGuildBan;
    }

    @BsonProperty("isSupportGuildBan")
    public void setSupportGuildBan(Boolean supportGuildBan) {
        this.supportGuildBan = supportGuildBan;
    }

    @BsonIgnore
    public Optional<Object> getProperty(@NotNull Property property, String idReference) {
        Predicate<PerServer> isItThisOne = lock -> lock.getGuildReference().equals(idReference);
        switch (property) {
            case KILL_TIMEOUT -> {
                return perServers.stream().filter(isItThisOne).findFirst()
                    .map(PerServer::getKillUnderTo);
            }
            case KILL_ATTEMPTS -> {
                return perServers.stream().filter(isItThisOne).findFirst()
                    .map(PerServer::getKillAttempts);
            }
            case KILL_END_DATE -> {
                return perServers.stream().filter(isItThisOne).findFirst()
                    .map(PerServer::getKillEndTimeAsDate);
            }
            case BOT_BAN -> {
                return Optional.ofNullable(botBanned);
            }
            case SUPPORT_BAN -> {
                return Optional.ofNullable(supportGuildBan);
            }
            case PER_SERVER ->
                throw new IllegalArgumentException(String.format("%S is not intended to be called.", property));
            default -> throw new IllegalArgumentException(String.format("%S is not a guild property.", property));
        }
    }

    @Override
    public String toString() {
        return "UserEntry{" +
            "idReference='" + idReference + '\'' +
            ", perServers=" + perServers +
            '}';
    }
}
