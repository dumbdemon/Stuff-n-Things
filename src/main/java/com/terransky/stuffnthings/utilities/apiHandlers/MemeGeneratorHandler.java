package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.terransky.stuffnthings.dataSources.DatumPojo;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemeGeneratorHandler extends Handler {

    private final String X_RapidAPI_Key;

    public MemeGeneratorHandler() {
        super("Meme-Generator");
        X_RapidAPI_Key = Config.Credentials.MEME_GENERATOR.getPassword();
    }

    public FileUpload generateMeme(String meme, String topText, String bottomText) throws InterruptedException, IOException {
        return generateMeme(meme, topText, bottomText, 50);
    }

    public FileUpload generateMeme(String meme, String topText, String bottomText, int fontSize) throws InterruptedException, IOException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            URI generatorURI = URI.create("https://ronreiter-meme-generator.p.rapidapi.com/meme?meme=" + meme +
                "&bottom=" + URLEncoder.encode(bottomText, StandardCharsets.UTF_8) +
                "&top=" + URLEncoder.encode(topText, StandardCharsets.UTF_8) +
                "&font=Impact&font_size=" + fontSize);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(generatorURI)
                .header("X-RapidAPI-Key", X_RapidAPI_Key)
                .header("X-RapidAPI-Host", "ronreiter-meme-generator.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<InputStream> response = getHttpClient(service).send(request, HttpResponse.BodyHandlers.ofInputStream());
            return FileUpload.fromData(response.body(), "meme-" + OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT) + ".png");
        }
    }

    public List<String> getMemes() throws InterruptedException, IOException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ronreiter-meme-generator.p.rapidapi.com/images"))
                .header("X-RapidAPI-Key", X_RapidAPI_Key)
                .header("X-RapidAPI-Host", "ronreiter-meme-generator.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = getHttpClient(service).send(request, HttpResponse.BodyHandlers.ofString());

            return new DatumPojo<String>(getObjectMapper().readValue(response.body(), new TypeReference<>() {
            })).datum().stream().toList();
        }
    }

    public String getRandomMeme(long seed) throws InterruptedException, IOException {
        List<String> memes = getMemes();
        return memes.get(new Random(seed).nextInt(memes.size()));
    }
}
