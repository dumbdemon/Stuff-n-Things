package com.terransky.stuffnthings.games.Bingo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terransky.stuffnthings.utilities.command.Formatter;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class State {

    private String row1;
    private String row2;
    private String row3;
    private String row4;
    private String row5;

    protected State() {
        row1 = "";
        row2 = "";
        row3 = "";
        row4 = "";
        row5 = "";
    }

    protected String toStringArray(boolean[] booleans) {
        Objects.requireNonNull(booleans);
        StringBuilder response = new StringBuilder();
        for (Boolean aBoolean : booleans) {
            response.append(aBoolean ? "1" : "0").append(" ");
        }

        return response.substring(0, response.length() - 1);
    }

    protected String toStringArray(int[] integers) {
        Objects.requireNonNull(integers);
        StringBuilder response = new StringBuilder();
        for (int anInt : integers) {
            response.append(anInt).append(" ");
        }

        return response.substring(0, response.length() - 1);
    }

    public String getRow1() {
        return row1;
    }

    public void setRow1(String row1) {
        this.row1 = row1;
    }

    public String getRow2() {
        return row2;
    }

    public void setRow2(String row2) {
        this.row2 = row2;
    }

    public String getRow3() {
        return row3;
    }

    public void setRow3(String row3) {
        this.row3 = row3;
    }

    public String getRow4() {
        return row4;
    }

    public void setRow4(String row4) {
        this.row4 = row4;
    }

    public String getRow5() {
        return row5;
    }

    public void setRow5(String row5) {
        this.row5 = row5;
    }


    @NotNull
    @BsonIgnore
    @JsonIgnore
    protected int[] toIntArray(String str) {
        Objects.requireNonNull(str);
        String[] array = str.split(" ");
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = Integer.parseInt(array[i]);
        }

        return intArray;
    }

    @NotNull
    @BsonIgnore
    @JsonIgnore
    protected boolean[] toBooleanArray(String str) {
        Objects.requireNonNull(str);
        int[] array = toIntArray(str);
        boolean[] booleans = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            booleans[i] = array[i] == 1;
        }
        return booleans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State that = (State) o;
        return getRow1().equals(that.getRow1()) &&
            getRow2().equals(that.getRow2()) &&
            getRow3().equals(that.getRow3()) &&
            getRow4().equals(that.getRow4()) &&
            getRow5().equals(that.getRow5());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow1(), getRow2(), getRow3(), getRow4(), getRow5());
    }

    @Override
    public String toString() {
        return Formatter.getNameOfClass(this.getClass()) + "{" +
            "row1='" + row1 + '\'' +
            ", row2='" + row2 + '\'' +
            ", row3='" + row3 + '\'' +
            ", row4='" + row4 + '\'' +
            ", row5='" + row5 + '\'' +
            '}';
    }
}
