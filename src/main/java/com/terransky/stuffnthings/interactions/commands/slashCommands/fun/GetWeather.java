package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.neovisionaries.i18n.CountryCode;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.openWeather.Current;
import com.terransky.stuffnthings.dataSources.openWeather.OpenWeatherData;
import com.terransky.stuffnthings.dataSources.openWeather.Weather;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.OpenWeatherHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.DegreeToQuadrant;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetWeather extends SlashCommandInteraction {

    public GetWeather() {
        super("weather", "Get the weather for a specific location.",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 1, 16, 27),
            parseDate(2025, 12, 29, 2, 25)
        );
        addSubcommandGroups(
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
    public boolean isWorking() {
        return !StuffNThings.getConfig().getTokens().getOpenWeatherKey().isEmpty();
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
                    weatherData = getUSWeatherData(event, subcommand, handler);
                    if (weatherData == null) return;
                    where = weatherData.getGeoData().getNameReadable();
                }
                case "by-coordinates" -> {
                    double lat = event.getOption("latitude", 33.44, OptionMapping::getAsDouble);
                    double lon = event.getOption("longitude", -94.04, OptionMapping::getAsDouble);
                    weatherData = handler.getWeatherData(lat, lon);
                    where = String.format("[%s, %s]", lat, lon);
                }
                default -> {
                    weatherData = getGlobalWeatherData(event, handler);
                    if (weatherData == null) return;

                    where = weatherData.getGeoData().getNameReadable();
                }
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(GetWeather.class).error("Couldn't get location data.", e);
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this,
                    Formatter.getLinkButtonSection(
                        StuffNThings.getConfig().getCore().getReportingUrl(), "Location provided is invalid. Please try again.\nIf this continues, please make a report."
                    ),
                    BotColors.ERROR)
            ).queue();
            return;
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("error during network operation", e);
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this, Responses.NETWORK_OPERATION)
            ).queue();
            return;
        }

        Current current = weatherData.getCurrent();
        DegreeToQuadrant toQuadrant = new DegreeToQuadrant();
        if (current == null) {
            event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(this, "No Weather data given")).queue();
            return;
        }

        List<ContainerChildComponent> children = new ArrayList<>();

        if (!current.getWeather().isEmpty()) {
            Weather weather = current.getWeather().get(0);
            children.add(Section.of(
                Thumbnail.fromUrl(weather.getIconURL()),
                TextDisplay.ofFormat("Current :: **%s**%n%n", weather.getMain())
            ));
        }
        if (current.getRain() != null) {
            children.add(TextDisplay.ofFormat("**%smm** of rain in the last hour%n", current.getRain().get_1h()));
        }
        if (current.getSnow() != null) {
            children.add(TextDisplay.ofFormat("**%smm** of snow in the last hour", current.getSnow().get_1h()));
        }

        children.add(TextDisplay.ofFormat("## Timezone\n%s", weatherData.getTimezone()));
        children.add(TextDisplay.ofFormat("## Current Time\n%s", current.getDtAsTimeStamp()));
        children.add(TextDisplay.ofFormat("## Sunrise\n%s", current.getSunriseAsTimeStamp(Timestamp.SHORT_TIME)));
        children.add(TextDisplay.ofFormat("## Sunset\n%s", current.getSunsetAsTimestamp(Timestamp.SHORT_TIME)));
        children.add(Separator.createInvisible(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## Temperature (Actual)\n%s", current.getTempsAsString()));
        children.add(TextDisplay.ofFormat("## Dew Point\n%s", current.getDewPiontsAsString()));
        children.add(TextDisplay.ofFormat("## Temperature (Feels Like)\n%s", current.getFeelsLikesAsString()));
        children.add(TextDisplay.ofFormat("## Pressure\n%s", String.format("**%s** hPa", current.getPressure())));
        children.add(TextDisplay.ofFormat("## Humidity\n%s", String.format("**%s**%%", current.getHumidity())));
        children.add(TextDisplay.ofFormat("## Clouds\n%s", String.format("**%s**%%", current.getClouds())));
        children.add(TextDisplay.ofFormat("## UV Index\n%s", String.format("**%s**", current.getUvi())));
        children.add(TextDisplay.ofFormat("## Visibility\n%s", String.format(current.getVisibilityAsString())));
        children.add(TextDisplay.ofFormat("## Wind Speed\n%s", current.getWindSpeedAsString()));

        if (current.getWindGust() != null) {
            children.add(TextDisplay.ofFormat("## Wind Gust\n%s", current.getWindGustAsString()));
        }

        children.add(TextDisplay.ofFormat("## Wind Direction\n%s", String.format("**%s**° (%s)", (int) (double) current.getWindDeg(), toQuadrant.getQuadrantName(current.getWindDeg()))));

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(String.format("Weather for %s", where), children)).queue();
    }

    @Nullable
    private OpenWeatherData getGlobalWeatherData(@NotNull SlashCommandInteractionEvent event, OpenWeatherHandler handler)
        throws IOException, InterruptedException {
        OpenWeatherData weatherData;
        String city = event.getOption("city", OptionMapping::getAsString);
        assert city != null;
        String state = event.getOption("state", OptionMapping::getAsString);
        String userCode = event.getOption("country-code", OptionMapping::getAsString);
        CountryCode code = CountryCode.getByCode(userCode, false);
        if (code == null) {
            event.getHook().sendMessageComponents(getBadUserCodeEmbed(userCode)).queue();
            return null;
        }

        weatherData = handler.getWeatherData(city, state, code);
        if (weatherData == null) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this, List.of(
                    TextDisplay.of("No location data returned from API. Did you type the location right?"),
                    TextDisplay.ofFormat("## City\n%s", city),
                    TextDisplay.ofFormat("## State\n%s", state != null ? state : "*None Provided*"),
                    TextDisplay.ofFormat("## Country\n%s", code.getAlpha2())
                ), BotColors.ERROR)
            ).queue();
            return null;
        }
        return weatherData;
    }

    @Nullable
    private OpenWeatherData getUSWeatherData(@NotNull SlashCommandInteractionEvent event, @NotNull String subcommand,
                                             OpenWeatherHandler handler) throws IOException {
        OpenWeatherData weatherData;
        if (subcommand.equals("us")) {
            int zipcode = event.getOption("zipcode", 90210, OptionMapping::getAsInt);
            weatherData = handler.getWeatherData(zipcode);
        } else {
            String zipcode = event.getOption("zipcode", OptionMapping::getAsString);
            String userCode = event.getOption("country-code", OptionMapping::getAsString);
            CountryCode code = CountryCode.getByCode(userCode, false);
            if (code == null) {
                event.getHook().sendMessageComponents(getBadUserCodeEmbed(userCode)).queue();
                return null;
            }
            weatherData = handler.getWeatherData(zipcode, code);
        }
        return weatherData;
    }

    @NotNull
    private Container getBadUserCodeEmbed(String userCode) {
        return StandardResponse.getResponseContainer(this, List.of(
            TextDisplay.of("Country code given was invalid."),
            TextDisplay.ofFormat("## Given\n%s", userCode)
        ), BotColors.ERROR);
    }
}
