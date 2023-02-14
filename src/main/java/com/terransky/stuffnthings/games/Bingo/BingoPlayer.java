package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.games.Player;
import net.dv8tion.jda.api.entities.User;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * An Object representing a BINGO player
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
public class BingoPlayer extends Player<Number> {

    @BsonIgnore
    @JsonIgnore
    private final static int FLOOR = 1;
    @BsonIgnore
    @JsonIgnore
    private final static int CEIL = 100;
    @BsonIgnore
    @JsonIgnore
    private final int GRID_SIZE = 5;
    private int[][] board;
    private boolean[][] checks;

    public BingoPlayer(@NotNull User user) {
        super(user);
        this.board = new int[GRID_SIZE][GRID_SIZE];
        generateBoard(user.getIdLong() | new Date().getTime());
        this.checks = new boolean[GRID_SIZE][GRID_SIZE];
    }

    public static int getFLOOR() {
        return FLOOR;
    }

    public static int getCEIL() {
        return CEIL;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
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
                do {
                    number = random.nextInt(FLOOR, CEIL);
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
    public boolean checkBoard(int number) {
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

    public boolean[][] getChecks() {
        return checks;
    }

    public void setChecks(boolean[][] checks) {
        this.checks = checks;
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

    private boolean checkFirstRow() {
        return checks[0][0] && checks[0][1] && checks[0][2] && checks[0][3] && checks[0][4];
    }

    private boolean checkSecondRow() {
        return checks[1][0] && checks[1][1] && checks[1][2] && checks[1][3] && checks[1][4];
    }

    private boolean checkThirdRow() {
        return checks[2][0] && checks[2][1] && checks[2][3] && checks[2][4];
    }

    private boolean checkFourthRow() {
        return checks[3][0] && checks[3][1] && checks[3][2] && checks[3][3] && checks[3][4];
    }

    private boolean checkFifthRow() {
        return checks[4][0] && checks[4][1] && checks[4][2] && checks[4][3] && checks[4][4];
    }

    /**
     * Checks if the player won vertically.
     *
     * @return True if the player has won
     */
    private boolean checkVerticalWin() {
        return checkColB() ||
            checkColI() ||
            checkColN() ||
            checkColG() ||
            checkColO();
    }

    private boolean checkColB() {
        return checks[0][0] && checks[1][0] && checks[2][0] && checks[3][0] && checks[4][0];
    }

    private boolean checkColI() {
        return checks[0][1] && checks[1][1] && checks[2][1] && checks[3][1] && checks[4][1];
    }

    private boolean checkColN() {
        return checks[0][2] && checks[1][2] && checks[3][2] && checks[4][2];
    }

    private boolean checkColG() {
        return checks[0][3] && checks[1][3] && checks[2][3] && checks[3][3] && checks[4][3];
    }

    private boolean checkColO() {
        return checks[0][4] && checks[1][4] && checks[2][4] && checks[3][4] && checks[4][4];
    }

    /**
     * Checks if the player won diagonally, horizontally, or vertically.
     *
     * @return True if the player has won
     */
    private boolean checkWinner() {
        return checkDiagonalWin() || checkHorizontalWin() || checkVerticalWin();
    }


    public String getWinMethod() {
        if (checkDiagonalWin())
            return "Diagonally (" + getDiagonalWinMethod() + ")";
        if (checkHorizontalWin())
            return "Horizontally (" + getHorizontalWinMethod() + ")";
        return "Vertically (" + getVerticalWinMethod() + ")";
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
        if (checkColB())
            return "B";
        if (checkColI())
            return "I";
        if (checkColN())
            return "N";
        if (checkColG())
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
