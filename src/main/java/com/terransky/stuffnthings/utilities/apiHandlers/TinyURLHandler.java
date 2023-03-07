package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terransky.stuffnthings.dataSources.tinyURL.ErrorTinyURLResponse;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLForm;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLResponse;
import com.terransky.stuffnthings.dataSources.tinyURL.ValidTinyURLResponse;
import com.terransky.stuffnthings.utilities.general.Config;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The handler for TinyURL's API
 */
public class TinyURLHandler extends Handler {

    public TinyURLHandler() {
        super("TinyURL");
    }

    /**
     * Get a shorten url
     *
     * @param tinyURLForm The body {@link com.terransky.stuffnthings.interfaces.Pojo POJO} to be sent to the API
     * @return A {@link TinyURLResponse}
     */
    @JsonIgnore
    public TinyURLResponse sendRequest(@NotNull TinyURLForm tinyURLForm) {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tinyurl.com/create?api_token=" + Config.Credentials.TINYURL.getPassword()))
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tinyURLForm.getAsJsonString()))
                .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                return getObjectMapper().readValue(response.body(), ErrorTinyURLResponse.class);
            }

            return getObjectMapper().readValue(response.body(), ValidTinyURLResponse.class);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
