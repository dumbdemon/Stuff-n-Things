package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitForm;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitResponse;
import com.terransky.stuffnthings.utilities.general.Config;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JokeSubmitHandler {

    private final ObjectMapper MAPPER = new ObjectMapper();

    public JokeSubmitResponse submitJoke(JokeSubmitForm submission) {
        Objects.requireNonNull(submission);
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .executor(service)
                .build();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v2.jokeapi.dev/submit" + (Config.isTestingMode() ? "?dry-run" : "")))
                .POST(HttpRequest.BodyPublishers.ofString(submission.getAsJsonString()))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return MAPPER.readValue(response.body(), JokeSubmitResponse.class);
        } catch (InterruptedException | IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error in submitting joke", e);
            return new JokeSubmitResponse()
                .withError(true)
                .withMessage("Error occurred during operation! Joke Submission status unknown.")
                .withTimestamp(OffsetDateTime.now().toInstant().toEpochMilli());
        }
    }
}
