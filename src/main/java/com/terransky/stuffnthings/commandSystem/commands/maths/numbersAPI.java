package com.terransky.stuffnthings.commandSystem.commands.maths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.dataSources.NumbersAPI.NumbersAPIData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ICommandSlash;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class numbersAPI implements ICommandSlash {

    private final NavigableMap<Integer, Integer> dayLimits = new TreeMap<>();
    private final Logger log = LoggerFactory.getLogger(numbersAPI.class);

    {
        dayLimits.put(1, 31);
        dayLimits.put(2, 29);
        dayLimits.put(3, 31);
        dayLimits.put(4, 30);
        dayLimits.put(5, 31);
        dayLimits.put(6, 30);
        dayLimits.put(7, 31);
        dayLimits.put(8, 31);
        dayLimits.put(9, 30);
        dayLimits.put(10, 31);
        dayLimits.put(11, 30);
        dayLimits.put(12, 31);
    }

    @Override
    public String getName() {
        return "number-facts";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Get some fun info on a number", """
            Random facts about numbers! How nerdy/geeky can you get? Leaving any option empty will return a random fact of that category.
            Facts are provided by [NumbersAPI](http://numbersapi.com).
            """, Mastermind.DEVELOPER,
            SlashModule.MATHS,
            format.parse("10-11-2022_20:45"),
            format.parse("1-12-2022_12:37")
        );

        metadata.addSubcommands(
            new SubcommandData("number", "A fact about a number.")
                .addOption(OptionType.NUMBER, "n-number", "A number"),
            new SubcommandData("math", "A math fact about a number.")
                .addOption(OptionType.NUMBER, "m-number", "A number"),
            new SubcommandData("date", "A random historical fact on a particular month and day.")
                .addOptions(
                    new OptionData(OptionType.INTEGER, "month", "The month.")
                        .setRequiredRange(1, 12),
                    new OptionData(OptionType.INTEGER, "day", "The day.")
                        .setRequiredRange(1, 31)
                ),
            new SubcommandData("year", "A random historical fact during a particular year.")
                .addOption(OptionType.NUMBER, "year", "A year.")
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given");
        String theUrl = "http://numbersapi.com/";
        EmbedBuilder response = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());

        switch (subcommand) {
            case "number" -> apiNumber(event, theUrl, response);
            case "math" -> apiMath(event, theUrl, response);
            case "date" -> apiDate(event, theUrl, response);
            case "year" -> apiYear(event, theUrl, response);
        }
    }

    private void apiYear(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> year = Optional.ofNullable(event.getOption("year", OptionMapping::getAsDouble));

        if (year.isPresent())
            theUrl += (year.get() + "/year").replace(".0/", "/");
        else
            theUrl += "random/year";

        log.debug("The url is %s.".formatted(theUrl));
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText();
        String date = numbersAPIData.getDate();
        Double actYear = year.orElse(numbersAPIData.getNumber());

        if (date == null) {
            date = "Year of";
        } else if (date.split(" ").length > 1) {
            date += ",";
        }

        String yearStr;
        if (actYear < 0d) {
            yearStr = Math.abs(actYear) + " BC";
        } else yearStr = String.valueOf(actYear);

        event.getHook().sendMessageEmbeds(
            eb.setTitle("Random Year Fact")
                .setDescription(text)
                .addField("Date", "%s %s".formatted(date, yearStr).replace(".0", ""), false)
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

        log.debug("The url is %s.".formatted(theUrl));
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText();
        double year = numbersAPIData.getYear();

        if (uMonth.isEmpty()) {
            String[] tempStr = text.split(" ");
            monthString = tempStr[0];
            day = Integer.parseInt(tempStr[1]
                .replace("st", "")
                .replace("nd", "")
                .replace("rd", "")
                .replace("th", "")
            );
        }

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Date Fact")
                .setDescription(text)
                .addField("Date", "%s %s, %s".formatted(monthString, day, year).replace(".0", ""), false)
                .build()
        ).queue();
    }

    private void apiMath(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> ifNumber = Optional.ofNullable(event.getOption("m-number", OptionMapping::getAsDouble));
        if (ifNumber.isPresent())
            theUrl += (ifNumber.get() + "/math").replace(".0/", "/");
        else
            theUrl += "random/math";

        log.debug("The url is %s.".formatted(theUrl));
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText();
        double actNumber = numbersAPIData.getNumber();

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Math Fact")
                .setDescription(text)
                .addField("Number", String.valueOf(actNumber).replace(".0", ""), false)
                .build()
        ).queue();
    }

    private void apiNumber(@NotNull SlashCommandInteractionEvent event, String theUrl, EmbedBuilder eb) throws IOException {
        Optional<Double> number = Optional.ofNullable(event.getOption("n-number", OptionMapping::getAsDouble));

        if (number.isPresent())
            theUrl += String.valueOf(number.get()).replace(".0", "");
        else
            theUrl += "random";

        log.debug("The url is %s.".formatted(theUrl));
        URL request = new URL(theUrl);
        HttpURLConnection numbersAPI = (HttpURLConnection) request.openConnection();
        numbersAPI.addRequestProperty("Content-Type", "application/json");

        NumbersAPIData numbersAPIData = new ObjectMapper().readValue(numbersAPI.getInputStream(), NumbersAPIData.class);
        String text = numbersAPIData.getText();
        double actNumber = numbersAPIData.getNumber();

        event.getHook().sendMessageEmbeds(
            eb.setTitle("NumbersAPI Number Fact")
                .setDescription(text)
                .addField("Number", String.valueOf(actNumber).replace(".0", ""), false)
                .build()
        ).queue();
    }
}
