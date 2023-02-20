package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Objects;

public class BoardState extends State {

    @SuppressWarnings("unused")
    public BoardState() {
        super();
    }

    public BoardState(int[][] matrix) {
        Objects.requireNonNull(matrix);
        setRow1(toStringArray(matrix[0]));
        setRow2(toStringArray(matrix[1]));
        setRow3(toStringArray(matrix[2]));
        setRow4(toStringArray(matrix[3]));
        setRow5(toStringArray(matrix[4]));
    }

    @BsonIgnore
    @JsonIgnore
    public int[][] getBoard(final int size) {
        int[][] board = new int[size][size];
        board[0] = toIntArray(getRow1());
        board[1] = toIntArray(getRow2());
        board[2] = toIntArray(getRow3());
        board[3] = toIntArray(getRow4());
        board[4] = toIntArray(getRow5());

        return board;
    }
}
