package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLForm;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLResponse;
import com.terransky.stuffnthings.utilities.general.Config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The handler for TinyURL's API
 */
public class TinyURLHandler extends TinyURLForm {

    @JsonIgnore
    private final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Builder for <a href="https://tinyurl.com/app/">TinyURLs</a> API request packet
     *
     * @param url A Url.
     * @throws MalformedURLException Thrown when the URL is not valid.
     * @throws URISyntaxException    Thrown when the URL is not valid.
     */
    public TinyURLHandler(String url) throws MalformedURLException, URISyntaxException {
        super(url);
    }

    /**
     * Get a shorten url
     *
     * @return A {@link TinyURLResponse}
     */
    @JsonIgnore
    public TinyURLResponse sendRequest() {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .executor(service)
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tinyurl.com/create?api_token=" + Config.Credentials.TINYURL.getPassword()))
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getAsJsonString()))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return MAPPER.readValue(response.body(), TinyURLResponse.class);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
