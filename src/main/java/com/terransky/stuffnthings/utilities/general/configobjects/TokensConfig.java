package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class TokensConfig {

    private TinyUrlToken tinyUrl;
    private UserPassword kitsuIo;
    private String openWeatherKey;
    private String memeGeneratorKey;

    TokensConfig() {
    }

    public TinyUrlToken getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(TinyUrlToken tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public UserPassword getKitsuIo() {
        return kitsuIo;
    }

    public void setKitsuIo(UserPassword kitsuIo) {
        this.kitsuIo = kitsuIo;
    }

    public String getOpenWeatherKey() {
        return openWeatherKey;
    }

    public void setOpenWeatherKey(String openWeatherKey) {
        this.openWeatherKey = openWeatherKey;
    }

    public String getMemeGeneratorKey() {
        return memeGeneratorKey;
    }

    public void setMemeGeneratorKey(String memeGeneratorKey) {
        this.memeGeneratorKey = memeGeneratorKey;
    }
}
