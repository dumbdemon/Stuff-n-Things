package com.terransky.stuffnthings.dataSources.kitsu.entries.enums;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
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

    public static Subtype getSubtypeByCode(@NotNull String code) {
        switch (code) {
            case "ONA" -> {
                return ONA;
            }
            case "OVA" -> {
                return OVA;
            }
            case "TV" -> {
                return TV;
            }
            case "movie" -> {
                return MOVIE;
            }
            case "music" -> {
                return MUSIC;
            }
            case "special" -> {
                return SPECIAL;
            }
            case "doujin" -> {
                return DOUJIN;
            }
            case "manga" -> {
                return MANGA;
            }
            case "manhua" -> {
                return MANHUA;
            }
            case "manhwa" -> {
                return MANHWA;
            }
            case "novel" -> {
                return NOVEL;
            }
            case "oel" -> {
                return OEL;
            }
            case "oneshot" -> {
                return ONESHOT;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }

    public String getCode() {
        return code;
    }
}
