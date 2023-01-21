package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuAuth;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuAuthError;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeKitsuData;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaKitsuData;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.Relationships;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.categories.CategoriesKitsuData;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.LogList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
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
        if (!credentials.isDefault() && upsertAuthorizationToken()) {
            KitsuAuth kitsuAuth = MAPPER.readValue(KITSU_AUTH, KitsuAuth.class);
            token = String.format("%s %s", kitsuAuth.getTokenType(), kitsuAuth.getAccessToken());
        }
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
                log.info("Auth found: reading file");
                KitsuAuth auth = MAPPER.readValue(KITSU_AUTH, KitsuAuth.class);
                if (!auth.isExpired()) {
                    log.warn("Auth is still valid for {} days: skipping request", auth.getDaysUntilExpired());
                    return true;
                }
                log.info("Auth is invalid: requesting new auth");

                rootNode.put("grant_type", "refresh_token");
                rootNode.put("refresh_token", auth.getRefreshToken());
            } else {
                log.info("Auth not found: creating file");
                if (credentials.isDefault()) {
                    log.warn("Auth request canceled: no credentials present");
                    return false;
                }

                rootNode.put("grant_type", "password");
                rootNode.put("username", credentials.getUsername());
                rootNode.put("password", credentials.getPassword());
            }

            HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://kitsu.io/api/oauth/token"))
                .POST(HttpRequest.BodyPublishers.ofString(rootNode.toPrettyString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            if (code != 200 && code - 500 < 0) {
                KitsuAuthError authError = MAPPER.readValue(response.body(), KitsuAuthError.class);
                log.error("Auth request failed: [{}] {}", authError.getError(), authError.getErrorDescription());
                return false;
            } else if (code - 500 >= 0) {
                log.error("Auth request failed: server error");
                return false;
            }

            KitsuAuth auth = MAPPER.readValue(response.body(), KitsuAuth.class);

            BufferedWriter writer = new BufferedWriter(new FileWriter(KITSU_AUTH));
            writer.append(auth.getPrettyString(MAPPER));
            writer.close();
            log.info("Auth request successful: absolute path of new auth is {}", KITSU_AUTH.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            log.error("{}; {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
            return false;
        } finally {
            service.shutdownNow();
            System.gc();
        }
        return true;
    }

    public MangaKitsuData getManga(@NotNull String search) throws IOException, LoginException {
        URL manga = new URL(BASE_URL + "manga?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(manga), MangaKitsuData.class);
    }

    public AnimeKitsuData getAnime(@NotNull String search) throws IOException, LoginException {
        URL anime = new URL(BASE_URL + "anime?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(anime), AnimeKitsuData.class);
    }

    public CategoriesKitsuData getCategories(@NotNull Relationships relationships) throws IOException, LoginException {
        URL genres = new URL(relationships.getCategories().getLinks().getRelated());
        return MAPPER.readValue(getInputStreamOf(genres), CategoriesKitsuData.class);
    }

    /**
     * Get the {@link InputStream} for Kitsu.io.
     *
     * @param url A Kitsu.io API endpoint URL
     * @return An {@link InputStream}
     * @throws IOException    If the URL is bad or the {@link InputStream} could not obtained.
     * @throws LoginException If the credentials are invalid or not provided. Or an err occured on server side.
     */
    @NotNull
    private InputStream getInputStreamOf(@NotNull URL url) throws IOException, LoginException {
        HttpURLConnection kitsuConnection = (HttpURLConnection) url.openConnection();
        kitsuConnection.addRequestProperty("Accept", "application/vnd.api+json");
        if (!credentials.isDefault())
            kitsuConnection.addRequestProperty("Authorization", String.format("Bearer %s", token));
        switch (kitsuConnection.getResponseCode()) {
            case 200 -> {
                return kitsuConnection.getInputStream();
            }
            case 400 -> throw new IllegalArgumentException("Bad Request - malformed request");
            case 401 -> throw new LoginException("Unauthorized - invalid or no authentication details provided");
            case 404 -> throw new IOException("Not Found - resource does not exist");
            case 406 -> throw new IllegalArgumentException("Not Acceptable - invalid Accept header");
            default -> throw new LoginException("Server Error");
        }
    }
}
