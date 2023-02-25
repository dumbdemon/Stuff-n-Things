package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.utilities.general.Config;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ICanHazDadJokeHandler {

    public ICanHazDadJokeData getDadJoke() {
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .executor(service)
                .build();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://icanhazdadjoke.com/"))
                .setHeader("User-Agent", Config.getBotUserAgent())  //https://icanhazdadjoke.com/api#custom-user-agent
                .setHeader("Accept", "application/json")
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return new ObjectMapper().readValue(response.body(), ICanHazDadJokeData.class);
        } catch (InterruptedException | IOException e) {
            LoggerFactory.getLogger(getClass()).error("Unable to get joke", e);
            return new ICanHazDadJokeData()
                .withId("-1")
                .withJoke("Unable to get joke. Sorry!");
        }
    }
}
