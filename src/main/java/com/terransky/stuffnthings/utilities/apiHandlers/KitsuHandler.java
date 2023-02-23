package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.kitsu.Datum;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuAuth;
import com.terransky.stuffnthings.dataSources.kitsu.PasswordKitsuAuthForm;
import com.terransky.stuffnthings.dataSources.kitsu.RefreshKitsuAuthForm;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeKitsuData;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaKitsuData;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.Relationships;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.categories.CategoriesKitsuData;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.utilities.general.Config;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handler for Kitsu.io API requests
 */
@SuppressWarnings("SpellCheckingInspection")
public class KitsuHandler {

    public static final String FILE_NAME = "kitsuToken.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(KitsuHandler.class);
    private static final File KITSU_AUTH = new File(FILE_NAME);
    private static final Config.Credentials credentials = Config.Credentials.KITSU_IO;
    private final String BASE_URL = "https://kitsu.io/api/edge/";
    private final String token;

    /**
     * API handler for Kitsu.io's API
     */
    public KitsuHandler() {
        Optional<KitsuAuth> kitsuAuth = DatabaseManager.INSTANCE.getKitsuAuth();
        token = kitsuAuth.map(KitsuAuth::getBearerString).orElse(null);
    }

    /**
     * Obtain or update an authorization token for <a href="">Kitsu.io</a> API.
     *
     * @return True if the process was successful.
     * @throws IllegalArgumentException If {@link Config.Credentials#isDefault()} returns true.
     */
    public static boolean upsertAuthorizationToken() throws IllegalArgumentException {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .executor(service)
                .build();

            String requestBody;
            Optional<KitsuAuth> oldAuth = DatabaseManager.INSTANCE.getKitsuAuth();

            if (oldAuth.isPresent()) {
                log.info("Auth found: reading auth");
                KitsuAuth auth = oldAuth.get();
                if (!auth.isExpired()) {
                    log.warn("Auth is still valid for {} days: skipping request", auth.getDaysUntilExpired());
                    return true;
                }
                log.info("Auth is invalid: requesting new auth");

                requestBody = new RefreshKitsuAuthForm()
                    .setRefreshToken(auth.getRefreshToken())
                    .getAsJsonString();
            } else {
                log.info("Auth not found: creating new auth");
                if (credentials.isDefault()) {
                    log.warn("Auth request canceled: no credentials present");
                    return false;
                }

                requestBody = new PasswordKitsuAuthForm()
                    .setUsername(credentials.getUsername())
                    .setPassword(credentials.getPassword())
                    .getAsJsonString();
            }

            HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Content-Type", "application/json")
                .uri(URI.create("https://kitsu.io/api/oauth/token"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            KitsuAuth auth;
            int code = response.statusCode();
            if (code != 200 && code - 500 < 0) {
                auth = MAPPER.readValue(response.body(), KitsuAuth.class);
                log.error("Auth request failed: [{}] {}", auth.getError(), auth.getErrorDescription());
                return false;
            } else if (code - 500 >= 0) {
                log.error("Auth request failed: server error");
                return false;
            }

            auth = MAPPER.readValue(response.body(), KitsuAuth.class);

            if (DatabaseManager.INSTANCE.uploadKitsuAuth(auth)) {
                log.info("Auth request successful: uploaded to database");
                auth.saveAsJsonFile(KITSU_AUTH);
                log.info("Auth file saved: the absolute path of new auth is {}", KITSU_AUTH.getAbsolutePath());
                return true;
            } else return false;
        } catch (IOException | InterruptedException e) {
            log.error("Auth request failed", e);
            return false;
        }
    }

    /**
     * Get manga from Kitsu.io
     *
     * @param search Query for search
     * @return A {@link MangaKitsuData}
     * @throws IOException If an i/o exception occurs
     */
    public MangaKitsuData getManga(@NotNull String search) throws IOException {
        URL manga = new URL(BASE_URL + "manga?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(manga), MangaKitsuData.class);
    }

    /**
     * Get anime from Kitsu.io
     *
     * @param search Query for search
     * @return A {@link AnimeKitsuData}
     * @throws IOException If an i/o exception occurs
     */
    public AnimeKitsuData getAnime(@NotNull String search) throws IOException {
        URL anime = new URL(BASE_URL + "anime?filter%5Btext%5D=" + search.toLowerCase().replaceAll(" ", "%20"));
        return MAPPER.readValue(getInputStreamOf(anime), AnimeKitsuData.class);
    }

    /**
     * Get the categories of an anime or manga
     *
     * @param relationships {@link Relationships} from a {@link Datum}
     * @return A {@link CategoriesKitsuData}
     * @throws IOException If an i/o exception occurs
     */
    public CategoriesKitsuData getCategories(@NotNull Relationships relationships) throws IOException {
        URL genres = new URL(relationships.getCategories().getLinks().getRelated());
        return MAPPER.readValue(getInputStreamOf(genres), CategoriesKitsuData.class);
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
        if (token != null)
            kitsuConnection.addRequestProperty("Authorization", token);
        switch (kitsuConnection.getResponseCode()) {
            case 200 -> {
                return kitsuConnection.getInputStream();
            }
            case 400 -> throw new IllegalArgumentException("Bad Request - malformed request");
            case 401 -> throw new IOException("Unauthorized - invalid or no authentication details provided");
            case 404 -> throw new IOException("Not Found - resource does not exist");
            case 406 -> throw new IllegalArgumentException("Not Acceptable - invalid Accept header");
            default -> throw new IOException("Server Error");
        }
    }
}
