package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.terransky.stuffnthings.dataSources.kitsu.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.LogList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("SpellCheckingInspection")
public class KitsuHandler {

    public static final String FILE_NAME = "kitsuToken.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(KitsuHandler.class);
    private static final File KITSU_AUTH = new File(FILE_NAME);
    private static final Config.Credentials credentials = Config.Credentials.KITSU_IO;
    private final String BASE_URL = "https://kitsu.io/api/edge/";
    private String token;

    /**
     * API handler for Kitsu.io's API
     */
    public KitsuHandler() throws IOException {
        if (!credentials.isDefault() && upsertAuthorizationToken())
            token = MAPPER.readValue(KITSU_AUTH, KitsuAuth.class).getAccessToken();
    }

    /**
     * Obtain or update an authorization token for <a href="">Kitsu.io</a> API.
     *
     * @return True if the process was successful.
     * @throws IllegalArgumentException If {@link Config.Credentials#isDefault()} returns true.
     */
    public static boolean upsertAuthorizationToken() throws IllegalArgumentException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(service)
            .build();
        try {
            ObjectNode rootNode = MAPPER.createObjectNode();

            if (KITSU_AUTH.exists()) {
                KitsuAuth auth = MAPPER.readValue(KITSU_AUTH, KitsuAuth.class);
                if (auth.getExpiresAt().compareTo(new Date()) > 0) {
                    log.warn(String.format("Kitsu.io token is still valid for %s days.",
                        (int) ChronoUnit.DAYS.between(new Date().toInstant(), auth.getExpiresAt().toInstant())));
                    return false;
                }

                rootNode.put("grant_type", "refresh_token");
                rootNode.put("refresh_token", auth.getRefreshToken());
            } else {
                if (credentials.isDefault())
                    throw new IllegalArgumentException("No username or password is present. Please add them to the config.");

                rootNode.put("grant_type", "password");
                rootNode.put("username", credentials.getUsername());
                rootNode.put("password", credentials.getPassword());
            }

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kitsu.io/api/oauth/token"))
                .POST(HttpRequest.BodyPublishers.ofString(rootNode.toPrettyString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400 || response.statusCode() == 401) {
                KitsuAuthError authError = MAPPER.readValue(response.body(), KitsuAuthError.class);
                log.error(String.format("%s: %s", authError.getError(), authError.getErrorDescription()));
                return false;
            } else if (response.statusCode() - 500 >= 0) {
                log.error("Server error on Kitsi.io side.");
                return false;
            }

            KitsuAuth auth = MAPPER.readValue(response.body(), KitsuAuth.class);

            BufferedWriter writer = new BufferedWriter(new FileWriter(KITSU_AUTH));
            writer.append(auth.getPrettyString(MAPPER));
            writer.close();
        } catch (IOException | InterruptedException e) {
            log.error(String.format("%s; %s", e.getClass().getName(), e.getMessage()));
            LogList.error(Arrays.asList(e.getStackTrace()), log);
            return false;
        } finally {
            service.shutdownNow();
            System.gc();
        }
        return true;
    }

    public MangaKitsuData getManga(@NotNull String search) throws IOException {
        URL manga = new URL(BASE_URL + "manga?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(manga), MangaKitsuData.class);
    }

    public AnimeKitsuData getAnime(@NotNull String search) throws IOException {
        URL anime = new URL(BASE_URL + "anime?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(anime), AnimeKitsuData.class);
    }

    public KitsuGenres getGenres(URL url) throws IOException {
        return MAPPER.readValue(getInputStreamOf(url), KitsuGenres.class);
    }

    /**
     * Get the {@link InputStream} for Kitsu.io.
     *
     * @param url A Kitsu.io API endpoint URL
     * @return An {@link InputStream}
     * @throws IOException If the URL is bad or the {@link InputStream} could not obtained.
     */
    @NotNull
    private InputStream getInputStreamOf(@NotNull URL url) throws IOException {
        HttpURLConnection kitsuConnection = (HttpURLConnection) url.openConnection();
        kitsuConnection.addRequestProperty("Accept", "application/vnd.api+json");
        if (!credentials.isDefault())
            kitsuConnection.addRequestProperty("Authorization", String.format("Bearer %s", token));
        return kitsuConnection.getInputStream();
    }
}
