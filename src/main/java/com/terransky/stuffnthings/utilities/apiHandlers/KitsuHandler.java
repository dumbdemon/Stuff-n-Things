package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.kitsu.AnimeKitsuData;
import com.terransky.stuffnthings.dataSources.kitsu.MangaKitsuData;
import com.terransky.stuffnthings.utilities.general.Config;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("SpellCheckingInspection")
public class KitsuHandler {

    private final ObjectMapper MAPPER = new ObjectMapper();
    private final String BASE_URL = "https://kitsu.io/api/edge/";

    public KitsuHandler() {
    }

    public static boolean getAuthorizationToken() {
        return false;
    }

    public MangaKitsuData getManga(String search) throws IOException {
        URL manga = new URL(BASE_URL + "manga");
        HttpURLConnection kitsuConnection = getConnection(manga);
        return MAPPER.readValue(kitsuConnection.getInputStream(), MangaKitsuData.class);
    }

    public AnimeKitsuData getAnime(String search) throws IOException {
        URL anime = new URL(BASE_URL + "anime");
        HttpURLConnection kitsuConnection = getConnection(anime);
        return MAPPER.readValue(kitsuConnection.getInputStream(), AnimeKitsuData.class);
    }

    @NotNull
    private HttpURLConnection getConnection(@NotNull URL manga) throws IOException {
        HttpURLConnection kitsuConnection = (HttpURLConnection) manga.openConnection();
        kitsuConnection.addRequestProperty("Content-Type", "application/json");
        kitsuConnection.addRequestProperty("Authorization", String.format("Bearer %s", Config.getKitsuToken()));
        return kitsuConnection;
    }
}
