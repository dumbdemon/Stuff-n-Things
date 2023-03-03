package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public enum Subtype {

    UNKNOWN("Unknown"),

    //Anime
    ONA("ONA"),
    OVA("OVA"),
    TV("TV"),
    MOVIE("Movie"),
    MUSIC("Music"),
    SPECIAL("Special"),

    //Manga
    DOUJIN("Doujin"),
    MANGA("Manga"),
    MANHUA("Manhua"),
    MANHWA("Manhwa"),
    NOVEL("Novel"),
    OEL("Web Comic"),
    ONESHOT("Oneshot");

    private final String code;

    Subtype(String code) {
        this.code = code;
    }

    public static Subtype getSubtypeByCode(String code) {
        if (code == null || code.isEmpty())
            return UNKNOWN;

        try {
            return Enum.valueOf(Subtype.class, code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public String getCode() {
        return code;
    }
}
