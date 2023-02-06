package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.utilities.general.Config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ICanHazDadJokeHandler {

    public ICanHazDadJokeData getDadJoke() throws IOException {
        URL iCanHazDadJoke = new URL("https://icanhazdadjoke.com/");
        HttpURLConnection dadJoke = (HttpURLConnection) iCanHazDadJoke.openConnection();
        dadJoke.addRequestProperty("User-Agent", Config.getBotUserAgent());  //https://icanhazdadjoke.com/api#custom-user-agent
        dadJoke.addRequestProperty("Accept", "application/json");
        return new ObjectMapper().readValue(dadJoke.getInputStream(), ICanHazDadJokeData.class);
    }
}
