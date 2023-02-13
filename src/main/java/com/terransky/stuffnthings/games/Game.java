package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terransky.stuffnthings.interfaces.Pojo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Game<T extends Player<?>> implements Pojo {

    private String channelId;
    private boolean isMultiplayer;
    private int playersMin;
    private int playersMax;
    private Host host;
    private List<T> players;

    protected Game() {
        this.players = new ArrayList<>();
    }

    protected Game(String channelId, Member member, Permission... requiredPermissions) {
        this(channelId, member, List.of(requiredPermissions));
    }

    protected Game(String channelId, Member member, Collection<Permission> requiredPermissions) {
        this.channelId = channelId;
        constructAndSetGameHost(member, requiredPermissions);
        this.players = new ArrayList<>();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public int getPlayersMin() {
        return playersMin;
    }

    public void setPlayersMin(int playersMin) {
        this.playersMin = playersMin;
    }

    public int getPlayersMax() {
        return playersMax;
    }

    public void setPlayersMax(int playersMax) {
        this.playersMax = playersMax;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    @JsonIgnore
    @BsonIgnore
    public void constructAndSetGameHost(@NotNull Member member, Collection<Permission> requiredPermissions) {
        this.host = new Host(member.getId(), requiredPermissions);
    }

    @JsonIgnore
    @BsonIgnore
    public void constructAndSetGameHost(@NotNull Member member, Permission... requiredPermissions) {
        this.host = new Host(member.getId(), requiredPermissions);
    }

    public List<T> getPlayers() {
        return players;
    }

    public void setPlayers(List<T> players) {
        this.players = players;
    }

    public void addPlayers(Collection<T> players) {
        this.players.addAll(players);
    }

    @SafeVarargs
    public final void addPlayers(T... players) {
        addPlayers(List.of(players));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game<?> game = (Game<?>) o;
        return isMultiplayer() == game.isMultiplayer() &&
            getPlayersMin() == game.getPlayersMin() &&
            getPlayersMax() == game.getPlayersMax() &&
            getChannelId().equals(game.getChannelId()) &&
            getHost().equals(game.getHost()) &&
            getPlayers().equals(game.getPlayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannelId(), isMultiplayer(), getPlayersMin(), getPlayersMax(), getHost(), getPlayers());
    }

    @Override
    public String toString() {
        return "Game{" +
            "channelId='" + channelId + '\'' +
            ", isMultiplayer=" + isMultiplayer +
            ", playersMin=" + playersMin +
            ", playersMax=" + playersMax +
            ", gameHost=" + host +
            ", players=" + players +
            '}';
    }
}
