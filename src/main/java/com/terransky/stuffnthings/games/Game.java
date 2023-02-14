package com.terransky.stuffnthings.games;

import com.terransky.stuffnthings.interfaces.Pojo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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
    private boolean isGameCompleted;

    protected Game() {
        this.players = new ArrayList<>();
    }

    protected Game(String channelId, Member member, Permission... requiredPermissions) {
        this(channelId, member, List.of(requiredPermissions));
    }

    protected Game(String channelId, @NotNull Member member, Collection<Permission> requiredPermissions) {
        this.channelId = channelId;
        this.host = new Host(member.getId(), requiredPermissions);
        this.players = new ArrayList<>();
        this.isGameCompleted = false;
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

    public List<T> getPlayers() {
        return players.stream().sorted().toList();
    }

    public void setPlayers(List<T> players) {
        this.players = players;
    }

    public void addPlayers(Collection<T> players) {
        this.players.addAll(players);
    }

    /**
     * Creates a Player object from a {@link Member}<br/>
     * This function is intended to be overridden and should not be called directly; otherwise, this function effectively does nothing.
     *
     * @param member A Member to add as a player
     */
    public void addPlayer(Member member) {
    }

    @SafeVarargs
    public final void addPlayers(T... players) {
        addPlayers(List.of(players));
    }

    public boolean isGameCompleted() {
        return isGameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        isGameCompleted = gameCompleted;
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
            getPlayers().equals(game.getPlayers()) &&
            isGameCompleted() == game.isGameCompleted();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannelId(), isMultiplayer(), getPlayersMin(), getPlayersMax(), getHost(), getPlayers(), isGameCompleted());
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
            ", isGameCompleted=" + isGameCompleted +
            '}';
    }
}
