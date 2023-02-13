package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.games.Game;
import net.dv8tion.jda.api.entities.Member;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.IOException;
import java.util.*;

@JsonPropertyOrder({
    "host",
    "channelId",
    "playersMin",
    "playersMax",
    "multiplayer",
    "calledNumbers",
    "players"
})
@SuppressWarnings("unused")
public class BingoGame extends Game<BingoPlayer> { //todo: Implement Letter calls, the commands, and database storage

    private List<Integer> calledNumbers;

    public BingoGame() {
        super();
        this.calledNumbers = new ArrayList<>();
    }

    public BingoGame(String channelId, Member member) {
        super(channelId, member);
        this.calledNumbers = new ArrayList<>();
        setMultiplayer(true);
        setPlayersMin(2);
        setPlayersMax(100);
    }

    public List<Integer> getCalledNumbers() {
        return calledNumbers;
    }

    public void setCalledNumbers(List<Integer> calledNumbers) {
        this.calledNumbers = List.copyOf(calledNumbers);
    }

    public List<BingoPlayer> play(long seed) {
        return new ArrayList<>() {{
            for (int number : getRandomNumbers(seed, 150)) {
                calledNumbers.add(number);
                for (BingoPlayer player : getPlayers()) {
                    if (player.checkBoard(number) && this.stream().noneMatch(bingoPlayer -> bingoPlayer.equals(player))) {
                        add(player);
                    }
                }
                if (!isEmpty()) break;
            }
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
    public void saveAsJsonFile() throws IOException {
        saveAsJsonFile("BingoGame On " + getChannelId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BingoGame game = (BingoGame) o;
        return getCalledNumbers().equals(game.getCalledNumbers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCalledNumbers());
    }
}
