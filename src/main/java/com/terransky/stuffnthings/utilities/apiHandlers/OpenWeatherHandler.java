package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherData;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherGeoData;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherHandler {

    /**
     * Valid as of {@value #VALID_TIME}
     */
    private static final List<CountryCode> TOP_25_COUNTRIES_USING_DISCORD = List.of(
        CountryCode.US,
        CountryCode.BR,
        CountryCode.PH,
        CountryCode.GB,
        CountryCode.IN,
        CountryCode.FR,
        CountryCode.CN,
        CountryCode.ID,
        CountryCode.CA,
        CountryCode.RU,
        CountryCode.DE,
        CountryCode.TR,
        CountryCode.JP,
        CountryCode.MX,
        CountryCode.VN,
        CountryCode.KR,
        CountryCode.AU,
        CountryCode.PL,
        CountryCode.IT,
        CountryCode.ES,
        CountryCode.CO,
        CountryCode.TW,
        CountryCode.PE,
        CountryCode.MY,
        CountryCode.TH
    );
    public static final String VALID_TIME = "Q4 2022";
    private final Config.Credentials CREDENTIALS = Config.Credentials.OPEN_WEATHER;
    private final String BASE_URL = String.format("https://api.openweathermap.org/data/3.0/onecall?lat={}&lon={}&exclude=minutely,hourly,daily&appid=%s",
        CREDENTIALS.getPassword()).replaceAll("\\{}", "%s");
    private final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Get a list of choices for countries.
     * <p>
     * Use {@link CountryCode#getByCode(String)} to get the appropriate enum.
     *
     * @return A {@link List} of {@link Command.Choice}s
     */
    @NotNull
    @Contract(" -> new")
    public static List<Command.Choice> getCountryCodesAsChoices() {
        return new ArrayList<>() {{
            for (CountryCode countryCode : TOP_25_COUNTRIES_USING_DISCORD) {
                add(new Command.Choice(countryCode.getName(), countryCode.getAlpha2()));
            }
        }};
    }

    public OpenWeatherData getWeatherData(double lat, double lon) throws IOException {
        URL request = new URL(String.format(BASE_URL, lat, lon));
        return MAPPER.readValue(request, OpenWeatherData.class);
    }

    public OpenWeatherData getWeatherData(int zipcode) throws IOException {
        String zicodeString = String.valueOf(zipcode);
        int difference = 5 - zicodeString.length();
        if (difference > 0)
            zicodeString = "0".repeat(difference) + zicodeString;
        return getWeatherData(zicodeString, CountryCode.US);
    }

    public OpenWeatherData getWeatherData(String zipcode, @NotNull CountryCode countryCode) throws IOException {
        URL request = new URL(String.format("http://api.openweathermap.org/geo/1.0/zip?zip=%s,%s&appid=%s",
            zipcode,
            countryCode.getAlpha2(),
            CREDENTIALS.getPassword()
        ));
        OpenWeatherGeoData geoData = MAPPER.readValue(request, OpenWeatherGeoData.class);
        return getWeatherData(geoData.getLat(), geoData.getLon())
            .setGeoData(geoData);
    }
}
