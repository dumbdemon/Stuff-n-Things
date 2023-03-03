package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terransky.stuffnthings.interfaces.Pojo;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public class Game<T extends Player> implements Pojo {

    private int playersMin;
    private String channelId;
    private String channelMention;
    private boolean isMultiplayer;
    private int playersMax;
    private Host host;
    private List<T> players;
    private String startTime;
    private String completedOn;
    private boolean isStarted;
    private boolean isGameCompleted;
    private boolean isDelayedStartGame;

    protected Game() {
        this.players = new ArrayList<>();
    }

    protected Game(Channel channel, Member host, Permission... requiredPermissions) {
        this(channel, host, List.of(requiredPermissions));
    }

    protected Game(@NotNull Channel channel, @NotNull Member host, Collection<Permission> requiredPermissions) {
        this.channelId = channel.getId();
        this.channelMention = channel.getAsMention();
        this.host = new Host(host);
        this.players = new ArrayList<>();
        this.isGameCompleted = false;
    }

    @JsonIgnore
    @BsonIgnore
    public int getMinimumPlayers() {
        return playersMin;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelMention() {
        return channelMention;
    }

    public void setChannelMention(String channelMention) {
        this.channelMention = channelMention;
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
        this.players = new ArrayList<>(players);
    }

    public void addPlayers(Collection<T> players) {
        this.players.addAll(players);
    }

    @SafeVarargs
    public final void addPlayers(T... players) {
        addPlayers(List.of(players));
    }

    /**
     * Adds a Player<br/>
     * This function is intended to be overridden and should not be called directly; otherwise, this function effectively does nothing.
     *
     * @param player A player object to add
     * @return If the player was successfully added
     */
    public boolean addPlayer(T player) {
        return false;
    }

    /**
     * Adds a Player object created from a {@link Member}<br/>
     * This function is intended to be overridden and should not be called directly; otherwise, this function effectively does nothing.<br/>
     * Example:
     * <pre><code>
     * public boolean addPlayer(Member member) {
     *     return addPlayers(new Player(member));
     * }
     * </code></pre>
     * This example above calls for the return of the {@link #addPlayers(Player[])} function with a single player parameter.
     *
     * @param member A Member to add as a player
     * @return If the player was successfully added
     */
    public boolean addPlayer(Member member) {
        return false;
    }

    @JsonIgnore
    @BsonIgnore
    public boolean hasMaxPlayers() {
        return players.size() == playersMax;
    }

    public long getPlayerSeed() {
        List<Long> playerIds = new ArrayList<>() {{
            players.forEach(player -> add(Long.parseLong(player.getId())));
        }};

        AtomicLong seed = new AtomicLong(new Date().getTime());
        playerIds.forEach(id -> seed.updateAndGet(value -> value | id));
        return seed.get();
    }

    @JsonIgnore
    @BsonIgnore
    public boolean isPlayerCountUnderMin() {
        return players.size() < playersMin;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonIgnore
    @BsonIgnore
    public String getStartTimeAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getStartTimeAsODT());
    }

    @JsonIgnore
    @BsonIgnore
    public String getStartTimeAsTimestampWithRelative() {
        return getStartTimeAsTimestampWithRelative(false);
    }

    public String getStartTimeAsTimestampWithRelative(boolean newLine) {
        return Timestamp.getDateAsTimestamp(getStartTimeAsODT()) + (newLine ? "\n" : " ") +
            "(" + Timestamp.getDateAsTimestamp(getStartTimeAsODT(), Timestamp.RELATIVE) + ")";
    }

    @JsonIgnore
    @BsonIgnore
    public OffsetDateTime getStartTimeAsODT() {
        return OffsetDateTime.parse(startTime);
    }

    public String getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(String completedOn) {
        this.completedOn = completedOn;
    }

    @JsonIgnore
    @BsonIgnore
    public String getCompletedOnAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getCompletedOnAsODT());
    }

    @JsonIgnore
    @BsonIgnore
    public String getCompletedOnAsTimestampWithRelative() {
        return getCompletedOnAsTimestampWithRelative(false);
    }

    public String getCompletedOnAsTimestampWithRelative(boolean newLine) {
        return Timestamp.getDateAsTimestamp(getCompletedOnAsODT()) + (newLine ? "\n" : " ") +
            "(" + Timestamp.getDateAsTimestamp(getCompletedOnAsODT(), Timestamp.RELATIVE) + ")";
    }

    @JsonIgnore
    @BsonIgnore
    public OffsetDateTime getCompletedOnAsODT() {
        return OffsetDateTime.parse(completedOn);
    }

    /**
     * Set the start time to now.
     */
    @JsonIgnore
    @BsonIgnore
    public void setStartTimeWithODT() {
        setStartTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
    }

    /**
     * Sets a delayed start time.
     *
     * @see OffsetDateTime#plus(long, TemporalUnit)
     */
    @JsonIgnore
    @BsonIgnore
    public void setStartTimeWithODT(long amountToAdd, TemporalUnit unit) {
        setStartTime(OffsetDateTime.now().plus(amountToAdd, unit).format(DateTimeFormatter.ISO_INSTANT));
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isDelayedStartGame() {
        return isDelayedStartGame;
    }

    public void setDelayedStartGame(boolean delayedStartGame) {
        isDelayedStartGame = delayedStartGame;
    }

    public boolean isGameCompleted() {
        return isGameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        setCompletedOn(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        isGameCompleted = gameCompleted;
    }

    @JsonIgnore
    @BsonIgnore
    public String getPlayersAsMentions() {
        List<String> mentions = new ArrayList<>() {{
            for (T player : getPlayers()) {
                add(player.getMention());
            }
        }};
        StringBuilder mentionString = new StringBuilder();
        for (String mention : mentions) {
            if (mentionString.length() + mention.length() > MessageEmbed.VALUE_MAX_LENGTH)
                return mentionString.substring(0, mentionString.length() - 2) + "…";
            mentionString.append(mention).append(", ");
        }
        return mentionString.substring(0, mentionString.length() - 2);
    }

    @JsonIgnore
    @BsonIgnore
    public boolean isMemberHost(Member member) {
        if (member == null) return false;
        return getHost().getHostId().equals(member.getId());
    }

    /**
     * Create a json file of this object with a custom file name.<br />
     * The name of the file follows this convention:<br/><code>{game name}+On+{channelId}.json</code>
     *
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    @Override
    public void saveAsJsonFile() throws IOException {
        String[] className = this.getClass().getName().split("\\.");
        saveAsJsonFile(className[className.length - 1] + " on " + getChannelId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game<?> game = (Game<?>) o;
        return isMultiplayer() == game.isMultiplayer() &&
            getPlayersMin() == game.getPlayersMin() &&
            getPlayersMax() == game.getPlayersMax() &&
            isStarted() == game.isStarted() &&
            isGameCompleted() == game.isGameCompleted() &&
            isDelayedStartGame() == game.isDelayedStartGame() &&
            getChannelId().equals(game.getChannelId()) &&
            getHost().equals(game.getHost()) &&
            getPlayers().equals(game.getPlayers()) &&
            getStartTime().equals(game.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannelId(),
            isMultiplayer(),
            getPlayersMin(),
            getPlayersMax(),
            getHost(),
            getPlayers(),
            getStartTime(),
            isStarted(),
            isGameCompleted(),
            isDelayedStartGame());
    }

    @Override
    public String toString() {
        return "Game{" +
            "channelId='" + channelId + '\'' +
            ", channelMention='" + channelMention + '\'' +
            ", isMultiplayer=" + isMultiplayer +
            ", playersMin=" + playersMin +
            ", playersMax=" + playersMax +
            ", host=" + host +
            ", players=" + players +
            ", startTime='" + startTime + '\'' +
            ", isStarted=" + isStarted +
            ", isGameCompleted=" + isGameCompleted +
            ", isDelayedStartGame=" + isDelayedStartGame +
            '}';
    }
}
