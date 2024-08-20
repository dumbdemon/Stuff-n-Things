package com.terransky.stuffnthings.utilities.apiHandlers;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ICanHazDadJokeHandler extends Handler {

    public ICanHazDadJokeHandler() {
        super("GetDadJoke");
    }

    public ICanHazDadJokeData getDadJoke() throws InterruptedException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://icanhazdadjoke.com/"))
                .setHeader("User-Agent", StuffNThings.getConfig().getCore().getUserAgent())  //https://icanhazdadjoke.com/api#custom-user-agent
                .setHeader("Accept", "application/json")
                .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            return getObjectMapper().readValue(response.body(), ICanHazDadJokeData.class);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Unable to get joke", e);
            return new ICanHazDadJokeData()
                .withId("-1")
                .withJoke("Unable to get joke. Sorry!");
        }
    }
}
