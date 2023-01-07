package com.terransky.stuffnthings.utilities.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLNoData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLRequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TinyURLHandler {

    private final String requestBody;

    public TinyURLHandler(@NotNull TinyURLRequestBuilder builder) {
        this(builder.build());
    }

    public TinyURLHandler(String requestBody) {
        this.requestBody = requestBody;
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
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            TinyURLNoData data = new ObjectMapper().readValue(response.body(), TinyURLNoData.class);
            if (data.getCode() == 0)
                return new ObjectMapper().readValue(response.body(), TinyURLData.class);

            return new TinyURLData().withCode(data.getCode())
                .withErrors(data.getErrors());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdownNow();
            System.gc();
        }
    }

    public String getRequestBody() {
        return requestBody;
    }
}
