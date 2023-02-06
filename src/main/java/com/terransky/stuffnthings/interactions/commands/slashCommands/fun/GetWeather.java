package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.neovisionaries.i18n.CountryCode;
import com.terransky.stuffnthings.dataSources.openWeather.Current;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherData;
import com.terransky.stuffnthings.dataSources.openWeather.Weather;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.OpenWeatherHandler;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.DegreeToQuadrant;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;

public class GetWeather implements ICommandSlash {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(getName(), "Get the weather for a specific location.", String.format("""
            Get the weather for a specific location. Due to Discord limitations and and the sheer amount of countries in existence, only the top 25 countries that use Discord will be provided.
            Top 25 list is valid as of %s.
            """, OpenWeatherHandler.VALID_TIME), Mastermind.DEVELOPER, CommandCategory.FUN,
            format.parse("1-2-2023_16:27"),
            format.parse("5-2-2023_19:19")
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
                                    .setMaxValue(99999)
                            ),
                        new SubcommandData("global", "Get weather data from a global zipcode")
                            .addOptions(
                                new OptionData(OptionType.STRING, "zipcode", "A zipcode", true),
                                new OptionData(OptionType.STRING, "country", "A country", true)
                                    .addChoices(OpenWeatherHandler.getCountryCodesAsChoices())
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
            if (subcommandGroup.equals("by-zipcode")) {
                if (subcommand.equals("us")) {
                    int zipcode = event.getOption("zipcode", 90210, OptionMapping::getAsInt);
                    weatherData = handler.getWeatherData(zipcode);
                } else {
                    String zipcode = event.getOption("zipcode", "E14", OptionMapping::getAsString);
                    CountryCode code = CountryCode.getByCode(event.getOption("country", "GB", OptionMapping::getAsString));
                    weatherData = handler.getWeatherData(zipcode, code);
                }
                where = weatherData.getGeoData().getName() + ", " + weatherData.getGeoData().getCountry();
            } else {
                double lat = event.getOption("latitude", 33.44, OptionMapping::getAsDouble);
                double lon = event.getOption("longitude", -94.04, OptionMapping::getAsDouble);
                weatherData = handler.getWeatherData(lat, lon);
                where = String.format("[%s, %s]", lat, lon);
            }
        } catch (IOException ignore) {
            event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                    .setTitle(getName())
                    .setDescription(String.format("Location provided is invalid. Please try again.%nIf this continues, [please make a report.](%s).",
                        Config.getErrorReportingURL()))
                    .setColor(EmbedColors.getError())
                    .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                    .build()
            ).queue();
            return;
        }

        Current current = weatherData.getCurrent();
        DegreeToQuadrant toQuadrant = new DegreeToQuadrant();
        EmbedBuilder main = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle(String.format("Weather for %s", where))
            .addField("Timezone", weatherData.getTimezone(), true)
            .addField("Current Time", current.getDtAsTimeStamp(), false)
            .addField("Sunrise", current.getSunriseAsTimeStamp(Timestamp.SHORT_TIME), true)
            .addField("Sunset", current.getSunsetAsTimestamp(Timestamp.SHORT_TIME), true)
            .addField("Temperature (Actual)", current.getTempsAsString(), false)
            .addField("Temperature (Feels Like)", current.getFeelsLikesAsString(), false)
            .addField("Pressure", String.format("**%s** hPa", current.getPressure()), true)
            .addField("Humidity", String.format("**%s**%%", current.getHumidity()), true)
            .addField("Clouds", String.format("**%s**%%", current.getClouds()), true)
            .addField("Dew Point", current.getDewPiontsAsString(), false)
            .addField("UV Index", String.format("**%s**", current.getUvi()), true)
            .addField("Visibility", String.format(current.getVisibilityAsString()), true)
            .addField("Wind Speed", current.getWindSpeedAsString(), true);

        if (current.getWindGust() != null) {
            main.addField("Wind Gust", current.getWindGustAsString(), true);
        }

        main.addField("Wind Direction", String.format("**%s**° (%s)", current.getWindDeg(), toQuadrant.getQuadrantName(current.getWindDeg())), true);

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
}
