package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.games.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An Object representing a BINGO player.<br/>
 * It is recommended to call {@link #loadPlayer()} before accessing data if obtained from the database.
 */
@JsonPropertyOrder({
    "name",
    "id",
    "mention",
    "score",
    "board",
    "checks"
})
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingoPlayer extends Player {

    @BsonIgnore
    @JsonIgnore
    private final int GRID_SIZE = 5;
    @BsonIgnore
    private int[][] board;
    @JsonIgnore
    private BoardState boardState;
    @BsonIgnore
    private boolean[][] checks;
    @JsonIgnore
    private ChecksState checksState;

    /**
     * Constructor Jackson and MongoDB
     */
    public BingoPlayer() {
        this.checks = new boolean[GRID_SIZE][GRID_SIZE];
    }

    /**
     * Constructor for a new Player
     *
     * @param member A {@link Member} to create the Player object for
     */
    public BingoPlayer(@NotNull Member member) {
        super(member);
        this.board = new int[GRID_SIZE][GRID_SIZE];
        generateBoard(member.getIdLong() | new Date().getTime());
        this.checks = new boolean[GRID_SIZE][GRID_SIZE];
        savePlayer();
    }

    /**
     * Load the board and checks to access data.
     */
    public void loadPlayer() {
        setBoard(boardState.getBoard(GRID_SIZE));
        setChecks(checksState.getChecks(GRID_SIZE));
    }

    /**
     * Save data to be uploaded to the database.
     */
    public void savePlayer() {
        setBoardState(new BoardState(board));
        setChecksState(new ChecksState(checks));
    }

    public int[][] getBoard() {
        return Arrays.copyOf(board, board.length);
    }

    public void setBoard(int[][] board) {
        this.board = Arrays.copyOf(board, board.length);
    }

    public State getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    @JsonIgnore
    @BsonIgnore
    public String getPrettyBoard() {
        StringBuilder prettyBoard = new StringBuilder("```\nB   I   N   G   O\n");
        for (int i = 0; i < GRID_SIZE; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < GRID_SIZE; j++) {
                String number = String.valueOf(board[i][j]);
                int diff = 2 - number.length();
                row.append("0".repeat(diff))
                    .append(board[i][j] == -1 ? "[]" : number)
                    .append(", ");
            }
            prettyBoard.append(row.substring(0, row.length() - 2)).append("\n");
        }

        return prettyBoard.append("```\n").toString();
    }

    @JsonIgnore
    @BsonIgnore
    public MessageEmbed.Field getNumberGotField() {
        List<String> numbersGotten = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (checks[i][j]) {
                    int diff = 2 - String.valueOf(board[i][j]).length();
                    numbersGotten.add(BingoGame.BingoLetter.getLetter(board[i][j]) + "0".repeat(diff) + board[i][j]);
                }
            }
        }

        StringBuilder numbersGot = new StringBuilder();
        numbersGotten.stream().sorted().forEach(number -> numbersGot.append(number).append(", "));
        String numbers = numbersGot.isEmpty() ? "None" : numbersGot.substring(0, numbersGot.length() - 2);
        return new MessageEmbed.Field("Numbers Gotten", numbers, false);
    }

    /**
     * Internal function to create a BINGO board
     *
     * @param seed The initial seed
     */
    private void generateBoard(long seed) {
        Random random = new Random(seed);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int number;
                BingoGame.BingoLetter letter = BingoGame.BingoLetter.getBingoLetterByIndex(j);
                do {
                    number = random.nextInt(letter.getCallFloor(), letter.getCallCeiling());
                } while (isOnBoard(number));
                board[i][j] = number;
            }
        }
        board[2][2] = -1;
    }

    /**
     * An internal check to see if the number is already on the player's board to avoid duplicate numbers.
     *
     * @param number The number to check
     * @return Tru if the number is on the player's board
     */
    private boolean isOnBoard(int number) {
        for (int[] row : board) {
            if (Arrays.stream(row).anyMatch(anInt -> anInt == number))
                return true;
        }
        return false;
    }

    /**
     * Checks the board if they have the number.
     *
     * @param number A Number to check
     * @return True if the player has won.
     */
    public boolean checkBoardForWinner(int number) {
        if (checkWinner())
            return false;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == number) {
                    checks[i][j] = true;
                    return checkWinner();
                }
            }
        }
        return false;
    }

    public boolean checkBoard(int number) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == number) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean[][] getChecks() {
        return Arrays.copyOf(checks, checks.length);
    }

    public void setChecks(boolean[][] checks) {
        this.checks = Arrays.copyOf(checks, checks.length);
    }

    /**
     * Checks if the player won diagonally.
     *
     * @return True if the player has won
     */
    private boolean checkDiagonalWin() {
        return checkTLBR() ||
            checkBLTR();
    }

    private boolean checkTLBR() {
        return checks[0][0] && checks[1][1] && checks[3][3] && checks[4][4];
    }

    private boolean checkBLTR() {
        return checks[4][0] && checks[3][1] && checks[1][3] && checks[0][4];
    }

    /**
     * Checks if the player won horizontally.
     *
     * @return True if the player has won
     */
    private boolean checkHorizontalWin() {
        return checkFirstRow() ||
            checkSecondRow() ||
            checkThirdRow() ||
            checkFourthRow() ||
            checkFifthRow();
    }

    private boolean checkRow(boolean[] check) {
        return checkRow(check, false);
    }

    private boolean checkRow(boolean[] check, boolean isMiddle) {
        Objects.requireNonNull(check);
        return check[0] && check[1] && (isMiddle || check[2]) && check[3] && check[4];
    }

    private boolean checkFirstRow() {
        return checkRow(checks[0]);
    }

    private boolean checkSecondRow() {
        return checkRow(checks[1]);
    }

    private boolean checkThirdRow() {
        return checkRow(checks[2], true);
    }

    private boolean checkFourthRow() {
        return checkRow(checks[3]);
    }

    private boolean checkFifthRow() {
        return checkRow(checks[4]);
    }

    /**
     * Checks if the player won vertically.
     *
     * @return True if the player has won
     */
    private boolean checkVerticalWin() {
        return checkColumnB() ||
            checkColumnI() ||
            checkColumnN() ||
            checkColumnG() ||
            checkColumnO();
    }

    private boolean checkColumn(int column) {
        if (column < 0 || column > GRID_SIZE)
            throw new IllegalArgumentException(String.format("Column must be between 0 and %d inclusively", GRID_SIZE));
        int i = 0;
        for (boolean[] check : checks) {
            if (check[column]) i++;
        }
        return column != (int) Math.floor(GRID_SIZE / 2.0) ? i == 5 : i == 4;
    }

    private boolean checkColumnB() {
        return checkColumn(0);
    }

    private boolean checkColumnI() {
        return checkColumn(1);
    }

    private boolean checkColumnN() {
        return checkColumn(2);
    }

    private boolean checkColumnG() {
        return checkColumn(3);
    }

    private boolean checkColumnO() {
        return checkColumn(4);
    }

    public boolean checkWinner() {
        return checkDiagonalWin() || checkHorizontalWin() || checkVerticalWin();
    }

    public State getChecksState() {
        return checksState;
    }

    public void setChecksState(ChecksState checksState) {
        this.checksState = checksState;
    }

    public String getWinMethod() {
        if (checkDiagonalWin())
            return "Diagonally (" + getDiagonalWinMethod() + ")";
        if (checkHorizontalWin())
            return "Horizontally (" + getHorizontalWinMethod() + ")";
        if (checkVerticalWin())
            return "Vertically (" + getVerticalWinMethod() + ")";
        return "N/A";
    }

    /**
     * Made to satisfy Mongo Driver. Ignore.
     */
    public void setWinMethod(String winMethod) {
    }

    @NotNull
    @Contract(pure = true)
    private String getDiagonalWinMethod() {
        if (checkTLBR())
            return "TL->BR";
        return "BL->TR";
    }

    @NotNull
    @Contract(pure = true)
    private String getHorizontalWinMethod() {
        if (checkFirstRow())
            return "1st Row";
        if (checkSecondRow())
            return "2nd Row";
        if (checkThirdRow())
            return "3rd Row";
        if (checkFourthRow())
            return "4th Row";
        return "5th Row";
    }

    @NotNull
    @Contract(pure = true)
    private String getVerticalWinMethod() {
        if (checkColumnB())
            return "B";
        if (checkColumnI())
            return "I";
        if (checkColumnN())
            return "N";
        if (checkColumnG())
            return "G";
        return "O";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BingoPlayer that = (BingoPlayer) o;
        return Arrays.deepEquals(getBoard(), that.getBoard()) && Arrays.deepEquals(getChecks(), that.getChecks());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), GRID_SIZE);
        result = 31 * result + Arrays.deepHashCode(getBoard());
        result = 31 * result + Arrays.deepHashCode(getChecks());
        return result;
    }
}
