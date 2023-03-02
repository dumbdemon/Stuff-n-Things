package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neovisionaries.i18n.CountryCode;
import com.terransky.stuffnthings.dataSources.DatumPojo;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherData;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherGeoData;
import com.terransky.stuffnthings.utilities.general.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The handler for OpenWeather's API
 */
public class OpenWeatherHandler extends Handler {

    private final Config.Credentials CREDENTIALS = Config.Credentials.OPEN_WEATHER;
    private final String BASE_URL = String.format("https://api.openweathermap.org/data/3.0/onecall?lat={}&lon={}&exclude=minutely,hourly,daily&appid=%s",
        CREDENTIALS.getPassword()).replaceAll("\\{}", "%s");

    public OpenWeatherHandler() {
        super("OpenWeather");
    }

    /**
     * Get weather date from geo coordinates
     *
     * @param lat The latitude
     * @param lon The longitude
     * @return An {@link OpenWeatherData}
     */
    public OpenWeatherData getWeatherData(double lat, double lon) {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(BASE_URL, lat, lon)))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return getObjectMapper().readValue(response.body(), OpenWeatherData.class);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Failed to get weather data", e);
        }
    }

    /**
     * Get weather data from a US zipcode
     *
     * @param zipcode A US zipcode
     * @return An {@link OpenWeatherData}
     * @throws IOException If an i/o exception occurs
     */
    public OpenWeatherData getWeatherData(int zipcode) throws IOException {
        String zicodeString = String.valueOf(zipcode);
        int difference = 5 - zicodeString.length();
        return getWeatherData("0".repeat(difference) + zicodeString, CountryCode.US);
    }

    /**
     * Get weather data using a zipcode of a country
     *
     * @param zipcode     A zipcode
     * @param countryCode A {@link CountryCode} enum
     * @return An {@link OpenWeatherData}
     */
    public OpenWeatherData getWeatherData(String zipcode, @NotNull CountryCode countryCode) {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://api.openweathermap.org/geo/1.0/zip?zip=%s,%s&appid=%s",
                    URLEncoder.encode(zipcode, StandardCharsets.UTF_8),
                    countryCode.getAlpha2(),
                    CREDENTIALS.getPassword()
                )))
                .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            OpenWeatherGeoData geoData = getObjectMapper().readValue(response.body(), OpenWeatherGeoData.class);
            return getWeatherData(geoData.getLat(), geoData.getLon())
                .setGeoData(geoData);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Failed to get weather data", e);
        }
    }

    /**
     * Get weather data using a location's name
     *
     * @param city  The city's name
     * @param state The state's name, if applicable
     * @param code  A {@link CountryCode}
     * @return An {@link OpenWeatherData}
     * @throws IOException If an i/o exception occurs
     */
    @Nullable
    public OpenWeatherData getWeatherData(String city, String state, @NotNull CountryCode code) throws IOException {
        try (ExecutorService service = Executors.newSingleThreadExecutor(getThreadFactory())) {
            HttpClient client = getHttpClient(service);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.openweathermap.org/geo/1.0/direct?q=" + city + "," + (state != null ? state + "," : "") + code.getAlpha2() +
                    "&limit=1&appid=" + CREDENTIALS.getPassword()))
                .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            DatumPojo<OpenWeatherGeoData> locations = new DatumPojo<>(getObjectMapper().readValue(response.body(), new TypeReference<>() {
            }));
            Optional<OpenWeatherGeoData> firstGeoData = locations.first(location -> code.getAlpha2().equals(location.getCountry()));

            if (firstGeoData.isEmpty())
                return null;

            OpenWeatherGeoData geoData = firstGeoData.get();
            return getWeatherData(geoData.getLat(), geoData.getLon())
                .setGeoData(geoData);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Failed to get weather data", e);
        }
    }
}
