package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.games.Game;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JsonPropertyOrder({
    "host",
    "channelId",
    "channelMention",
    "startTime",
    "verbose",
    "started",
    "delayedStartGame",
    "gameCompleted",
    "multiplayer",
    "delay",
    "playersMin",
    "playersMax",
    "playerSeed",
    "calledNumbers",
    "players"
})
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingoGame extends Game<BingoPlayer> { //todo: Implement Letter calls

    private List<Integer> calledNumbers;
    private long delay;
    private boolean verbose;

    public BingoGame() {
        super();
        this.calledNumbers = new ArrayList<>();
    }

    public BingoGame(Channel channel, Member member) {
        this(channel, member, 10);
    }

    public BingoGame(Channel channel, Member member, long minutesToDelay) {
        super(channel, member);
        this.calledNumbers = new ArrayList<>();
        this.delay = minutesToDelay;
        setMultiplayer(true);
        setDelayedStartGame(true);
        setStartTimeWithODT(minutesToDelay, java.time.temporal.ChronoUnit.MINUTES);
        setPlayersMin(2);
        setPlayersMax(100);
    }

    public List<Integer> getCalledNumbers() {
        return calledNumbers;
    }

    public void setCalledNumbers(List<Integer> calledNumbers) {
        this.calledNumbers = new ArrayList<>(calledNumbers);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public List<BingoPlayer> play(long seed) {
        return new ArrayList<>() {{
            for (int number : getRandomNumbers(seed, 150)) {
                calledNumbers.add(number);
                for (BingoPlayer player : getPlayers()) {
                    player.loadPlayer();
                    if (player.checkBoard(number)) {
                        add(player);
                    }
                    player.savePlayer();
                }
                if (!isEmpty()) break;
            }
            setGameCompleted(true);
        }};
    }

    /**
     * Generate an array of numbers.
     *
     * @param seed   The initial seed
     * @param amount The amount of number to generate.
     * @return An array of valid BINGO numbers
     */
    @JsonIgnore
    @BsonIgnore
    public int[] getRandomNumbers(long seed, int amount) {
        Random random = new Random(seed | amount | new Date().getTime());
        int[] numbers = new int[amount];
        for (int i = 0; i < amount; i++) {
            numbers[i] = random.nextInt(BingoPlayer.getFLOOR(), BingoPlayer.getCEIL());
        }
        return numbers;
    }

    @Override
    public boolean addPlayer(@NotNull Member member) {
        if (getPlayers().stream().anyMatch(bingoPlayer -> bingoPlayer.getId().equals(member.getId())))
            return false;

        if (hasMaxPlayers())
            return false;

        addPlayers(new BingoPlayer(member));
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BingoGame bingoGame = (BingoGame) o;
        return getDelay() == bingoGame.getDelay() &&
            getCalledNumbers().equals(bingoGame.getCalledNumbers()) &&
            isVerbose() == bingoGame.isVerbose();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCalledNumbers(), getDelay(), isVerbose());
    }

    @Override
    public String toString() {
        return "BingoGame{" +
            "calledNumbers=" + calledNumbers +
            ", delay=" + delay +
            ", verbose=" + verbose +
            '}';
    }
}
