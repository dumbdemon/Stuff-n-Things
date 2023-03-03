package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.games.Game;
import com.terransky.stuffnthings.utilities.command.Formatter;
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
    "completedOn",
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
    "called",
    "players"
})
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingoGame extends Game<BingoPlayer> {

    private List<String> calledNumbers;
    private long delay;
    private boolean verbose;

    public BingoGame() {
        super();
        this.calledNumbers = new ArrayList<>();
    }

    public BingoGame(Channel channel, Member host) {
        this(channel, host, 10);
    }

    public BingoGame(Channel channel, Member host, long minutesToDelay) {
        super(channel, host);
        this.calledNumbers = new ArrayList<>();
        this.delay = minutesToDelay;
        setMultiplayer(true);
        setDelayedStartGame(true);
        setStartTimeWithODT(minutesToDelay, java.time.temporal.ChronoUnit.MINUTES);
        setPlayersMin(2);
        setPlayersMax(100);
    }

    public List<String> getCalledNumbers() {
        return calledNumbers;
    }

    public void setCalledNumbers(List<String> calledNumbers) {
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
                String value = BingoLetter.getLetter(number) + number;
                if (!calledNumbers.contains(value)) {
                    calledNumbers.add(value);
                    for (BingoPlayer player : getPlayers()) {
                        player.loadPlayer();
                        if (player.checkBoardForWinner(number)) {
                            add(player);
                        }
                        player.savePlayer();
                    }
                    if (!isEmpty()) break;
                }
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
            numbers[i] = random.nextInt(BingoLetter.B.getCallFloor(), BingoLetter.O.getCallCeiling());
        }
        return numbers;
    }

    @JsonIgnore
    @BsonIgnore
    public LinkedHashMap<String, List<BingoPlayer>> getVerboseOrder() {
        return new LinkedHashMap<>() {{
            for (String key : calledNumbers) {
                int integer = Integer.parseInt(key.substring(1));
                List<BingoPlayer> value = new ArrayList<>() {{
                    for (BingoPlayer player : getPlayers()) {
                        if (player.checkBoard(integer))
                            add(player);
                    }
                }};

                put(key, value);
            }
        }};
    }

    @Override
    public boolean addPlayer(BingoPlayer player) {
        if (getPlayers().stream().anyMatch(bingoPlayer -> bingoPlayer.getId().equals(player.getId())))
            return false;

        if (hasMaxPlayers())
            return false;

        addPlayers(player);
        return true;
    }

    @Override
    public boolean addPlayer(@NotNull Member member) {
        return addPlayer(new BingoPlayer(member));
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

    enum BingoLetter {
        B(0, 20),
        I(1, 40),
        N(2, 60),
        G(3, 80),
        O(4, 100);

        private final int index;
        private final int callCeiling;


        BingoLetter(int index, int callCeiling) {
            this.index = index;
            this.callCeiling = callCeiling;
        }

        public static BingoLetter getBingoLetterByIndex(int index) {
            switch (index) {
                case 0 -> {
                    return B;
                }
                case 1 -> {
                    return I;
                }
                case 2 -> {
                    return N;
                }
                case 3 -> {
                    return G;
                }
                case 4 -> {
                    return O;
                }
                default ->
                    throw new IndexOutOfBoundsException(String.format("There is not a %s with that index", Formatter.getNameOfClass(BingoLetter.class)));
            }
        }

        @NotNull
        public static String getLetter(int number) {
            if (number <= 0) return "";
            List<BingoLetter> letters = EnumSet.allOf(BingoLetter.class).stream().sorted(new SortByCeiling()).toList();

            for (BingoLetter letter : letters) {
                if (number < letter.getCallCeiling()) {
                    return letter.get();
                }
            }

            return "";
        }

        @NotNull
        public String get() {
            return this.name();
        }

        public int getIndex() {
            return index;
        }

        public int getCallFloor() {
            return callCeiling - 19;
        }

        public int getCallCeiling() {
            return callCeiling;
        }

        static class SortByCeiling implements Comparator<BingoLetter> {
            @Override
            public int compare(BingoLetter firstLetter, BingoLetter secondLetter) {
                Objects.requireNonNull(firstLetter);
                Objects.requireNonNull(secondLetter);
                return Integer.compare(firstLetter.getCallCeiling(), secondLetter.getCallCeiling());
            }
        }
    }
}
