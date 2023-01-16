package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLNoData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLRequestData;
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

public class TinyURLHandler extends TinyURLRequestData {

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

    public TinyURLData sendRequest() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(service)
            .build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tinyurl.com/create?api_token=" + Config.getTinyURLToken()))
            .setHeader("accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(getRequestBody()))
            .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            TinyURLNoData data = MAPPER.readValue(response.body(), TinyURLNoData.class);
            if (data.getCode() != 0)
                return new TinyURLData().withCode(data.getCode())
                    .withErrors(data.getErrors());

            return MAPPER.readValue(response.body(), TinyURLData.class);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdownNow();
            System.gc();
        }
    }

    public String getRequestBody() {
        ObjectNode rootNode = MAPPER.createObjectNode();

        rootNode.put("url", getUrl());
        rootNode.put("domain", getDomain());
        rootNode.put("alias", getAlias());
        rootNode.put("tags", getTags());
        rootNode.put("expires_at", getExpiresAt() != null ? getExpiresAtAsString() : null);

        return rootNode.toPrettyString();
    }
}
