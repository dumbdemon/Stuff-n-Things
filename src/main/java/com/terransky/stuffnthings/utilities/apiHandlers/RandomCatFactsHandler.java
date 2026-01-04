package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RandomCatFactsHandler extends Handler {

    public RandomCatFactsHandler() {
        super("RandomCatFacts");
    }

    public List<String> getRandomCatFacts(int count)  throws IOException, InterruptedException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://meowfacts.herokuapp.com/%s", count > 1 ? ("?count=" + count) : "")))
                .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return getObjectMapper().readValue(response.body(), RandomCatFactsData.class).getData();
        }
    }


    static class RandomCatFactsData {

        @JsonProperty("data")
        @Valid
        private List<String> data;

        @JsonProperty("data")
        public List<String> getData() {
            return List.copyOf(data);
        }

        @JsonProperty("data")
        public void setData(List<String> data) {
            this.data = List.copyOf(data);
        }
    }
}
