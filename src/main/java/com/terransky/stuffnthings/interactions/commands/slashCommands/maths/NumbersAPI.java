package com.terransky.stuffnthings.interactions.commands.slashCommands.maths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.NumbersAPI.NumbersAPIData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.*;

public class NumbersAPI implements ICommandSlash {

    private final NavigableMap<Integer, Integer> dayLimits = new TreeMap<>() {{
        put(1, 31);
        put(2, 29);
        put(3, 31);
        put(4, 30);
        put(5, 31);
        put(6, 30);
        put(7, 31);
        put(8, 31);
        put(9, 30);
        put(10, 31);
        put(11, 30);
        put(12, 31);
    }};
    private final Logger log = LoggerFactory.getLogger(NumbersAPI.class);
    private final DecimalFormat intFormatter = new DecimalFormat("###");
    private final List<Command.Choice> monthChoices = new ArrayList<>() {{
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < 12; i++) {
            add(new Command.Choice(String.format("[%s] %s", (i + 1), months[i]), (i + 1)));
        }
    }};

    @Override
    public String getName() {
        return "number-facts";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Get some fun info on a number", """
            Random facts about numbers! How nerdy/geeky can you get? Leaving any option empty will return a random fact of that category.
            Facts are provided by [NumbersAPI](http://numbersapi.com).
            """, Mastermind.DEVELOPER,
            CommandCategory.MATHS,
            Metadata.parseDate(2022, 11, 10, 20, 45),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        )
            .addSubcommands(
                new SubcommandData("number", "A fact about a number.")
                    .addOption(OptionType.NUMBER, "n-number", "A number"),
                new SubcommandData("math", "A math fact about a number.")
                    .addOption(OptionType.NUMBER, "m-number", "A number"),
                new SubcommandData("date", "A random historical fact on a particular month and day.")
                    .addOptions(
                        new OptionData(OptionType.STRING, "month", "The month.")
                            .addChoices(monthChoices),
                        new OptionData(OptionType.INTEGER, "day", "The day.")
                            .setRequiredRange(1, 31)
                    ),
                new SubcommandData("year", "A random historical fact during a particular year.")
                    .addOption(OptionType.NUMBER, "year", "A year.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given");
        String theUrl = "http://numbersapi.com/";
        EmbedBuilder response = blob.getStandardEmbed();

        switch (subcommand) {
            case "number" -> apiNumber(event, theUrl, response);
            case "math" -> apiMath(event, theUrl, response);
            case "date" -> apiDate(event, theUrl, response);
            case "year" -> apiYear(event, theUrl, response);
        }
    }

    private void apiYear(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> ifYear = Optional.ofNullable(event.getOption("year", OptionMapping::getAsDouble));

        if (ifYear.isPresent())
            theUrl += (intFormatter.format(ifYear.get()) + "/year");
        else
            theUrl += "random/year";

        log.debug("The url is {}.", theUrl);
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText();
        String date = numbersAPIData.getDate();
        double year = ifYear.orElse(numbersAPIData.getNumber());

        if (date == null) {
            date = "Year of";
        } else if (date.split(" ").length > 1) {
            date += ",";
        }

        String yearStr;
        if (year < 0.0) {
            yearStr = intFormatter.format(Math.abs(year)) + " BC";
        } else yearStr = intFormatter.format(year);

        event.getHook().sendMessageEmbeds(
            eb.setTitle("Random Year Fact")
                .setDescription(text)
                .addField("Date", "%s %s".formatted(date, yearStr), false)
                .build()
        ).queue();
    }

    private void apiDate(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Integer> uMonth = Optional.ofNullable(event.getOption("month", OptionMapping::getAsInt));
        Optional<Integer> uDay = Optional.ofNullable(event.getOption("day", OptionMapping::getAsInt));
        String monthString = "";
        int month;
        int day = 0;
        int maxDays;
        Map.Entry<Integer, Integer> dayLimit;

        if ((uMonth.isEmpty() && uDay.isPresent()) || (uMonth.isPresent() && uDay.isEmpty())) {
            event.getHook().sendMessageEmbeds(
                eb.setTitle("NumbersAPI Date Fact")
                    .setDescription("Missing value. Both values must be entered.")
                    .addField("Option Given", uMonth.isPresent() ? "Month" : "Day", true)
                    .addField("Option Value", uMonth.isPresent() ? String.valueOf(uMonth.get()) : String.valueOf(uDay.get()), true)
                    .build()
            ).queue();
            return;
        }

        if (uMonth.isEmpty()) {
            theUrl += "random/date";
        } else {
            month = uMonth.get();
            day = uDay.get();
            monthString = new DateFormatSymbols().getMonths()[month - 1];
            dayLimit = dayLimits.floorEntry(month);
            maxDays = dayLimit.getValue();

            if (day > maxDays) {
                String maxDaysStr = month == 2 ? "at most 29" : String.valueOf(maxDays);

                event.getHook().sendMessageEmbeds(
                    eb.setTitle("NumbersAPI Date Fact")
                        .setDescription("You have entered an invalid day for %s. Remember there are only %s days in %s.".formatted(monthString, maxDaysStr, monthString))
                        .build()
                ).queue();
                return;
            }

            theUrl += "%s/%s/date".formatted(month, day);
        }

        log.debug("The url is {}.", theUrl);
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText(),
            year = intFormatter.format(numbersAPIData.getYear());

        if (uMonth.isEmpty()) {
            String[] tempStr = text.split(" ");
            monthString = tempStr[0];
            day = Integer.parseInt(tempStr[1].replaceAll("st|nd|rd|th", ""));
        }

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Date Fact")
                .setDescription(text)
                .addField("Date", "%s %s, %s".formatted(monthString, day, year), false)
                .build()
        ).queue();
    }

    private void apiMath(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> ifNumber = Optional.ofNullable(event.getOption("m-number", OptionMapping::getAsDouble));
        if (ifNumber.isPresent())
            theUrl += (intFormatter.format(ifNumber.get()) + "/math");
        else
            theUrl += "random/math";

        log.debug("The url is {}.", theUrl);
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText(),
            actNumber = intFormatter.format(numbersAPIData.getNumber());

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Math Fact")
                .setDescription(text)
                .addField("Number", actNumber, false)
                .build()
        ).queue();
    }

    private void apiNumber(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> number = Optional.ofNullable(event.getOption("n-number", OptionMapping::getAsDouble));

        if (number.isPresent())
            theUrl += intFormatter.format(number.get());
        else
            theUrl += "random";

        log.debug("The url is {}.", theUrl);
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText(),
            actNumber = intFormatter.format(numbersAPIData.getNumber());

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Number Fact")
                .setDescription(text)
                .addField("Number", actNumber, false)
                .build()
        ).queue();
    }
}
