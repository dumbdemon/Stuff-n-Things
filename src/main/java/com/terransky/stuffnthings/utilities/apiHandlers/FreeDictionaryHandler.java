package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.terransky.stuffnthings.dataSources.DatumPojo;
import com.terransky.stuffnthings.dataSources.freeDictionary.FreeDictionaryDatum;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FreeDictionaryHandler extends Handler {

    public FreeDictionaryHandler() {
        super("FreeDictionary");
    }

    public DatumPojo<FreeDictionaryDatum> getdefinitions(String word) throws IOException, InterruptedException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.dictionaryapi.dev/api/v2/entries/en/%s", word)))
                .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return new DatumPojo<>(getObjectMapper().readValue(response.body(), new TypeReference<>() {
            }));
        }
    }
}
