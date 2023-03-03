package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

@SuppressWarnings("unused")
public enum AgeRating {

    /**
     * Since not rated content could potentially be adult it is set as so.
     */
    NR("NR", "Not Rated", true),
    G("G", "General Audiences"),
    PG("PG", "Parental Guidance Suggested"),
    R("R", "Restricted", true),
    R18("R18", "Explicit", true);

    private final String code;
    private final String codename;
    private final boolean isAdult;

    AgeRating(String code, String codename) {
        this(code, codename, false);
    }

    AgeRating(String code, String codename, boolean isAdult) {
        this.code = code;
        this.codename = codename;
        this.isAdult = isAdult;
    }

    public static AgeRating getAgeRatingByCode(String code) {
        if (code == null || code.isEmpty())
            return NR;

        try {
            return Enum.valueOf(AgeRating.class, code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NR;
        }
    }

    public String getCode() {
        return code;
    }

    public String getCodename() {
        return codename;
    }

    public boolean isAdult() {
        return isAdult;
    }
}
