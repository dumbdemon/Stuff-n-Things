package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.neovisionaries.i18n.CountryCode;
import com.terransky.stuffnthings.dataSources.openWeather.Current;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherData;
import com.terransky.stuffnthings.dataSources.openWeather.Weather;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.OpenWeatherHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.DegreeToQuadrant;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetWeather implements ICommandSlash {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Get the weather for a specific location.", """
            Get the weather for a specific location.
            Examples of Country Codes: US (United States), GB (United Kingdom), FR (France), DE (Germany), etc.
            If you don't know your country's code, you can use [this website](https://www.iso.org/obp/ui/#search).
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-01T16:27Z"),
            Metadata.parseDate("2023-03-16T12:48Z")
        )
            .addSubcommandGroups(
                new SubcommandGroupData("by-coordinates", "Get the weather by coordinates.")
                    .addSubcommands(
                        new SubcommandData("coordinates", "Get the weather by coordinates.")
                            .addOptions(
                                new OptionData(OptionType.NUMBER, "latitude", "The latitude", true)
                                    .setRequiredRange(-360, 360),
                                new OptionData(OptionType.NUMBER, "longitude", "The longitude", true)
                                    .setRequiredRange(-360, 360)
                            )
                    ),
                new SubcommandGroupData("by-zipcode", "Get the weather by zipcode")
                    .addSubcommands(
                        new SubcommandData("us", "Get weather data from a US zipcode.")
                            .addOptions(
                                new OptionData(OptionType.INTEGER, "zipcode", "A zipcode", true)
                                    .setRequiredRange(0, 99999)
                            ),
                        new SubcommandData("global", "Get weather data from a global zipcode")
                            .addOptions(
                                new OptionData(OptionType.STRING, "zipcode", "A zipcode", true),
                                new OptionData(OptionType.STRING, "country-code", "A country code EX: US, GB", true)
                                    .setRequiredLength(2, 2)
                            )
                    ),
                new SubcommandGroupData("by-location", "Get the weather by the location's name")
                    .addSubcommands(
                        new SubcommandData("name", "Get the weather by the location's name")
                            .addOptions(
                                new OptionData(OptionType.STRING, "city", "The name of the city.", true),
                                new OptionData(OptionType.STRING, "country-code", "A country code EX: US, GB", true)
                                    .setRequiredLength(2, 2),
                                new OptionData(OptionType.STRING, "state", "The name of the state, if applicable.")
                            )
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        String subcommandGroup = event.getSubcommandGroup();
        String subcommand = event.getSubcommandName();
        if (subcommandGroup == null || subcommand == null)
            throw new DiscordAPIException("No subcommand group was given");
        String where;
        OpenWeatherData weatherData;
        OpenWeatherHandler handler = new OpenWeatherHandler();

        try {
            switch (subcommandGroup) {
                case "by-zipcode" -> {
                    if (subcommand.equals("us")) {
                        int zipcode = event.getOption("zipcode", 90210, OptionMapping::getAsInt);
                        weatherData = handler.getWeatherData(zipcode);
                    } else {
                        String zipcode = event.getOption("zipcode", OptionMapping::getAsString);
                        String userCode = event.getOption("country-code", OptionMapping::getAsString);
                        CountryCode code = CountryCode.getByCode(userCode, false);
                        if (code == null) {
                            event.getHook().sendMessageEmbeds(getBadUserCodeEmbed(blob, userCode)).queue();
                            return;
                        }
                        weatherData = handler.getWeatherData(zipcode, code);
                    }
                    where = weatherData.getGeoData().getNameReadable();
                }
                case "by-coordinates" -> {
                    double lat = event.getOption("latitude", 33.44, OptionMapping::getAsDouble);
                    double lon = event.getOption("longitude", -94.04, OptionMapping::getAsDouble);
                    weatherData = handler.getWeatherData(lat, lon);
                    where = String.format("[%s, %s]", lat, lon);
                }
                default -> {
                    String city = event.getOption("city", OptionMapping::getAsString);
                    assert city != null;
                    String state = event.getOption("state", OptionMapping::getAsString);
                    String userCode = event.getOption("country-code", OptionMapping::getAsString);
                    CountryCode code = CountryCode.getByCode(userCode, false);
                    if (code == null) {
                        event.getHook().sendMessageEmbeds(getBadUserCodeEmbed(blob, userCode)).queue();
                        return;
                    }

                    weatherData = handler.getWeatherData(city, state, code);
                    if (weatherData == null) {
                        event.getHook().sendMessageEmbeds(
                            blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
                                .setDescription("No location data returned from API. Did you type the location right?")
                                .addField("City", city, true)
                                .addField("State", state != null ? state : "*None Provided*", true)
                                .addField("Country", code.getAlpha2(), true)
                                .build()
                        ).queue();
                        return;
                    }

                    where = weatherData.getGeoData().getNameReadable();
                }
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(GetWeather.class).error("Couldn't get location data.", e);
            event.getHook().sendMessageEmbeds(
                blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
                    .setDescription(String.format("Location provided is invalid. Please try again.%nIf this continues, [please make a report](%s).",
                        Config.getErrorReportingURL()))
                    .build()
            ).queue();
            return;
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("error during network operation", e);
            event.getHook().sendMessageEmbeds(
                blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
                    .setDescription(Responses.NETWORK_OPERATION.getMessage())
                    .setColor(EmbedColor.ERROR.getColor())
                    .build()
            ).queue();
            return;
        }

        Current current = weatherData.getCurrent();
        DegreeToQuadrant toQuadrant = new DegreeToQuadrant();
        EmbedBuilder main = blob.getStandardEmbed(String.format("Weather for %s", where))
            .addField("Timezone", weatherData.getTimezone(), true)
            .addField("Current Time", current.getDtAsTimeStamp(), false)
            .addField("Sunrise", current.getSunriseAsTimeStamp(Timestamp.SHORT_TIME), true)
            .addField("Sunset", current.getSunsetAsTimestamp(Timestamp.SHORT_TIME), true)
            .addBlankField(true)
            .addField("Temperature (Actual)", current.getTempsAsString(), true)
            .addField("Dew Point", current.getDewPiontsAsString(), true)
            .addField("Temperature (Feels Like)", current.getFeelsLikesAsString(), true)
            .addField("Pressure", String.format("**%s** hPa", current.getPressure()), true)
            .addField("Humidity", String.format("**%s**%%", current.getHumidity()), true)
            .addField("Clouds", String.format("**%s**%%", current.getClouds()), true)
            .addField("UV Index", String.format("**%s**", current.getUvi()), true)
            .addField("Visibility", String.format(current.getVisibilityAsString()), true)
            .addField("Wind Speed", current.getWindSpeedAsString(), true);

        if (current.getWindGust() != null) {
            main.addField("Wind Gust", current.getWindGustAsString(), true);
        }

        main.addField("Wind Direction", String.format("**%s**° (%s)", (int) (double) current.getWindDeg(), toQuadrant.getQuadrantName(current.getWindDeg())), true);

        if (!current.getWeather().isEmpty()) {
            Weather weather = current.getWeather().get(0);
            main.setThumbnail(weather.getIconURL())
                .appendDescription(String.format("Current :: **%s**%n%n", weather.getMain()));
        }
        if (current.getRain() != null) {
            main.appendDescription(String.format("**%smm** of rain in the last hour%n", current.getRain().get_1h()));
        }
        if (current.getSnow() != null) {
            main.appendDescription(String.format("**%smm** of snow in the last hour", current.getSnow().get_1h()));
        }

        event.getHook().sendMessageEmbeds(main.build()).queue();
    }

    @NotNull
    private MessageEmbed getBadUserCodeEmbed(@NotNull EventBlob blob, String userCode) {
        return blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
            .setDescription("Country code given was invalid.")
            .addField("Given", userCode, false)
            .build();
    }
}
