package com.terransky.stuffnthings.utilities.apiHandlers;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitForm;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitResponse;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JokeSubmitHandler extends Handler {

    public JokeSubmitHandler() {
        super("JokeSubmission");
    }

    public JokeSubmitResponse submitJoke(JokeSubmitForm submission) throws InterruptedException {
        Objects.requireNonNull(submission);
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v2.jokeapi.dev/submit" + (StuffNThings.getConfig().getCore().getTestingMode() ? "?dry-run" : "")))
                .POST(HttpRequest.BodyPublishers.ofString(submission.getAsJsonString()))
                .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            return getObjectMapper().readValue(response.body(), JokeSubmitResponse.class);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error in submitting joke", e);
            return new JokeSubmitResponse()
                .withError(true)
                .withMessage("Error occurred during operation! Joke Submission status unknown.")
                .withTimestamp(OffsetDateTime.now().toInstant().toEpochMilli());
        }
    }
}
