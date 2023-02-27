package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.oxfordDictionary.OxfordData;
import com.terransky.stuffnthings.dataSources.oxfordDictionary.OxfordError;
import com.terransky.stuffnthings.dataSources.oxfordDictionary.Result;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Dictionary implements ICommandSlash {
    private static final int MAX_FIELDS = 25;
    private final Logger log = LoggerFactory.getLogger(Dictionary.class);
    private final NavigableMap<String, Locale> langCodes = new TreeMap<>() {{
        put("US English", Locale.forLanguageTag("en-us"));
        put("UK English", Locale.forLanguageTag("en-gb"));
        put("French", Locale.forLanguageTag("fr"));
        put("Gujarati", Locale.forLanguageTag("gu"));
        put("Hindi", Locale.forLanguageTag("hi"));
        put("Latvian", Locale.forLanguageTag("lv"));
        put("Romanian", Locale.forLanguageTag("ro"));
        put("Spanish", Locale.forLanguageTag("es"));
        put("Swahili", Locale.forLanguageTag("sw"));
        put("Tamil", Locale.forLanguageTag("ta"));
    }};
    private final List<Command.Choice> langChoices = new ArrayList<>() {{
        for (String lang : langCodes.keySet()) {
            add(new Command.Choice(lang, lang));
        }
    }};

    private static void run200(@NotNull SlashCommandInteractionEvent event, EmbedBuilder embedBuilder, Locale language, String word,
                               @NotNull HttpURLConnection oxfordConnection, @NotNull ObjectMapper om) throws IOException {
        OxfordData oxfordData = om.readValue(oxfordConnection.getInputStream(), OxfordData.class);
        List<MessageEmbed.Field> definitionFields = getDefinitionFields(oxfordData.getResults().stream().filter(it -> it.getWord().equalsIgnoreCase(word)).toList());
        int wordCount = definitionFields.size();

        if (wordCount == 0) {
            event.getHook().sendMessageEmbeds(
                embedBuilder.setTitle("Definition - %s".formatted(word.toUpperCase(language)))
                    .setDescription("%s is in the dictionary; however, there appears to be no definitions. Try using a different variation of the word."
                        .formatted(WordUtils.capitalize(word))
                    ).build()
            ).queue();
            return;
        }

        boolean moreThanOne = wordCount > 1;

        embedBuilder.setTitle("Definition - %s".formatted(word.toUpperCase(language)))
            .setDescription("There %s %d *%s* definition%s for *%s*.%s"
                .formatted(
                    moreThanOne ? "are" : "is",
                    wordCount,
                    language.getDisplayName(),
                    moreThanOne ? "s" : "",
                    word,
                    language.getDisplayName().contains("English") ? "" :
                        "\n\n**Warning**: You are searching in a language other than English. Certain characters may not show if your browser/device does not support it."
                )
            );

        int stopHere = Math.min(MAX_FIELDS, wordCount);
        for (int i = 0; i < stopHere; i++) {
            embedBuilder.addField(definitionFields.get(i));
        }

        if (wordCount > MAX_FIELDS) {
            EmbedBuilder ebOverflow = new EmbedBuilder(embedBuilder)
                .setColor(EmbedColors.getSecondary());
            for (int i = MAX_FIELDS; i < wordCount; i++) {
                ebOverflow.addField(definitionFields.get(i));
            }
            event.getHook().sendMessageEmbeds(embedBuilder.build(), ebOverflow.build()).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @NotNull
    private static List<MessageEmbed.Field> getDefinitionFields(@NotNull List<Result> results) {
        String baseString = "%s — *%s*.";
        return new ArrayList<>() {{
            results.forEach(result -> {
                    String returnedWord = result.getWord();
                    result.getLexicalEntries().forEach(lexicalEntry -> {
                            String lexicalCategory = lexicalEntry.getLexicalCategory().getText();
                            lexicalEntry.getEntries().forEach(entry ->
                                entry.getSenses().forEach(sense -> sense.getDefinitions().forEach(definition -> {
                                        add(getField(baseString, returnedWord, lexicalCategory, definition));
                                        sense.getSubsenses().forEach(subsense ->
                                            subsense.getDefinitions()
                                                .forEach(subDefinition ->
                                                    add(getField(("˪ " + baseString), returnedWord, lexicalCategory, subDefinition))
                                                )
                                        );
                                    })
                                )
                            );
                        }
                    );
                }
            );
        }};
    }

    @NotNull
    private static MessageEmbed.Field getField(String baseString, String word, String lexicalCategory, String definition) {
        return new MessageEmbed.Field(
            String.format(baseString, WordUtils.capitalize(word), lexicalCategory),
            "```%s```".formatted(definition),
            false
        );
    }

    @Override
    public String getName() {
        return "dictionary";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Look up a word in the dictionary in up to 9 different languages.", """
            Powered by Oxford Languages, this command returns all definitions of a given word in up to %d languages as long as it is within that language's lexicon.
                        
            WARNING: depending on the word it may return no definitions. Try a different variation of that word if it happens.
            """.formatted(langCodes.size()),
            Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate("2022-10-27T12:46Z"),
            Metadata.parseDate("2023-02-27T16:21Z")
        )
            .addOptions(
                new OptionData(OptionType.STRING, "word", "The word to look up.", true),
                new OptionData(OptionType.STRING, "language", "The source language to look up. US English is default.", false)
                    .addChoices(langChoices)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        String[] userWords = event.getOption("word", "", OptionMapping::getAsString).split(" ");
        Config.Credentials credentials = Config.Credentials.OXFORD;
        EmbedBuilder eb = blob.getStandardEmbed(getNameReadable())
            .setImage("https://languages.oup.com/wp-content/uploads/ol-logo-colour-300px-sfw.jpg");
        if (userWords.length > 1) {
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Only one word can be looked up at one time. Please try again.")
                    .build()
            ).queue();
            return;
        }
        if (userWords[0].equals(""))
            throw new DiscordAPIException("Required option [%s] was not given".formatted("word"));
        Map.Entry<String, Locale> language = langCodes.floorEntry(event.getOption("language", "US English", OptionMapping::getAsString));
        String toLookUp = userWords[0].toLowerCase(language.getValue());

        URL dictionary = new URL("https://od-api.oxforddictionaries.com/api/v2/entries/%s/%s?fields=definitions&strictMatch=false"
            .formatted(language.getValue().toLanguageTag().toLowerCase(), toLookUp));
        HttpURLConnection oxfordConnection = (HttpURLConnection) dictionary.openConnection();
        oxfordConnection.addRequestProperty("Accept", "application/json");
        oxfordConnection.addRequestProperty("app_id", credentials.getUsername());
        oxfordConnection.addRequestProperty("app_key", credentials.getPassword());
        int responseCode = oxfordConnection.getResponseCode();
        ObjectMapper om = new ObjectMapper();

        switch (responseCode) {
            case 200 -> run200(event, eb, language.getValue(), toLookUp, oxfordConnection, om);
            case 400 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription(("Unable to get the definition of [%s]. Make sure you have typed the word correctly," +
                            " If this message continues to appear, please report this incident [here](%s).")
                            .formatted(toLookUp.toUpperCase(language.getValue()), Config.getErrorReportingURL())
                        )
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("400 Bad Request: {}", message);
            }
            case 403 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("403 Authentication failed: {}", message);
            }
            case 404 -> event.getHook().sendMessageEmbeds(
                eb.setTitle("Definition - %s".formatted(toLookUp.toUpperCase(language.getValue())))
                    .setDescription("There are no *%s* definitions for [%s].\n\nEither the word does not exist or you have mistyped."
                        .formatted(language.getKey(), toLookUp.toUpperCase(language.getValue())))
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            case 500 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("Looks like something went wrong with the API. Please report this incident [here](%s)"
                            .formatted(Config.getErrorReportingURL()))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("500 Internal Server Error: {}", message);
            }
            case 414 -> event.getHook().sendMessageEmbeds(
                eb.setDescription("Your word can be at most 128 characters. Please look up a different word.")
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            case 502 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("The API server is currently down or being upgraded. Please try this command later.\n\n[Click me to check.](https://downforeveryoneorjustme.com/languages.oup.com?proto=https)")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("502 Bad Gateway: {}", message);
            }
            case 503, 504 -> {
                boolean isIt503 = responseCode == 503;
                String serviceUnavailable = "there appear to be too many requests in the queue";
                String gatewayTimeout = "something went wrong while in queue";
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("The API servers are up, but %s. Please wait a couple moments and try again.".formatted(isIt503 ? serviceUnavailable : gatewayTimeout))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("{} {}: {}", responseCode, isIt503 ? "Service Unavailable" : "Gateway timeout", message);
            }
        }

        oxfordConnection.disconnect();
    }

    @Override
    public boolean isWorking() {
        return !Config.Credentials.OXFORD.isDefault();
    }
}
