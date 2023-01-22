package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

import org.jetbrains.annotations.NotNull;

public enum AgeRating {

    NR("NR", "Not Rated"),
    G("G", "General Audiences"),
    PG("PG", "Parental Guidance Suggested"),
    R("R", "Restricted"),
    R18("R18", "Explicit");

    private final String code;
    private final String codename;

    AgeRating(String code, String codename) {
        this.code = code;
        this.codename = codename;
    }

    public static AgeRating getAgeRatingByCode(@NotNull String code) {
        switch (code) {
            case "G" -> {
                return G;
            }
            case "PG" -> {
                return PG;
            }
            case "R" -> {
                return R;
            }
            case "R18" -> {
                return R18;
            }
            default -> {
                return NR;
            }
        }
    }

    public String getCode() {
        return code;
    }

    public String getCodename() {
        return codename;
    }

    public static boolean checkIfAdult(AgeRating ageRating) {
        return ageRating == R ||
            ageRating == R18 ||
            ageRating == NR;
    }
}
