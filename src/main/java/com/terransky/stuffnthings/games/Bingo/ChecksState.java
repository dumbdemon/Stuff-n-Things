package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Objects;

public class ChecksState extends State {

    @SuppressWarnings("unused")
    public ChecksState() {
        super();
    }

    public ChecksState(boolean[][] matrix) {
        Objects.requireNonNull(matrix);
        setRow1(toStringArray(matrix[0]));
        setRow2(toStringArray(matrix[1]));
        setRow3(toStringArray(matrix[2]));
        setRow4(toStringArray(matrix[3]));
        setRow5(toStringArray(matrix[4]));
    }

    @BsonIgnore
    @JsonIgnore
    public boolean[][] getChecks(final int size) {
        boolean[][] checks = new boolean[size][size];
        checks[0] = toBooleanArray(getRow1());
        checks[1] = toBooleanArray(getRow2());
        checks[2] = toBooleanArray(getRow3());
        checks[3] = toBooleanArray(getRow4());
        checks[4] = toBooleanArray(getRow5());

        return checks;
    }
}
