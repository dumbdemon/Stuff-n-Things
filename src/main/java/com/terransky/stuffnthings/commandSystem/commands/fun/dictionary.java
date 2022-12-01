package com.terransky.stuffnthings.commandSystem.commands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.dataSources.oxfordDictionary.*;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

public class dictionary implements ISlashCommand {
    private final Logger log = LoggerFactory.getLogger(dictionary.class);
    private final NavigableMap<String, String> langCodes = new TreeMap<>();
    private final List<Command.Choice> langChoices = new ArrayList<>();

    {
        langCodes.put("US English", "en-us");
        langCodes.put("UK English", "en_gb");
        langCodes.put("French", "fr");
        langCodes.put("Gujarati", "gu");
        langCodes.put("Hindi", "hi");
        langCodes.put("Latvian", "lv");
        langCodes.put("Romanian", "ro");
        langCodes.put("Spanish", "es");
        langCodes.put("Swahili", "sw");
        langCodes.put("Tamil", "ta");

        for (String lang : langCodes.keySet()) {
            langChoices.add(new Command.Choice(lang, lang));
        }
    }

    @Override
    public String getName() {
        return "dictionary";
    }

    private static void run200(@NotNull SlashCommandInteractionEvent event, EmbedBuilder eb, EmbedBuilder ebOverflow, Map.Entry<String, String> language,
                               String toLookUp, @NotNull HttpURLConnection oxfordConnection, @NotNull ObjectMapper om) throws IOException {
        OxfordData oxfordData = om.readValue(oxfordConnection.getInputStream(), OxfordData.class);
        int wordCount = 0;
        String returnedWord = "";
        List<MessageEmbed.Field> fieldOverflow = new ArrayList<>();

        for (Result result : oxfordData.getResults().stream().filter(it -> it.getWord().equalsIgnoreCase(toLookUp)).toList()) {
            returnedWord = result.getWord();
            wordCount = getWordCount(eb, wordCount, returnedWord, fieldOverflow, result);
        }

        if (wordCount == 0) {
            event.getHook().sendMessageEmbeds(
                eb.setTitle("Definition - %s".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(language.getValue()))))
                    .setDescription("%s is in the dictionary; however, there appears to be no definitions. Try using a different variation of the word."
                        .formatted(WordUtils.capitalize(returnedWord))
                    ).build()
            ).queue();
            return;
        }

        boolean moreThanOne = wordCount > 1;

        eb.setTitle("Definition - %s".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(language.getValue()))))
            .setDescription("There %s %d *%s* definition%s for *%s*.%s"
                .formatted(
                    moreThanOne ? "are" : "is",
                    wordCount,
                    language.getKey(),
                    moreThanOne ? "s" : "",
                    returnedWord,
                    language.getKey().endsWith("English") ? "" : "\n\n**Warning**: You are searching in a language other than English. Certain characters may not show if your browser/device does not support it."
                )
            );

        if (fieldOverflow.size() > 0) {
            ebOverflow.setTitle("Definition - %s cont.".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(language.getValue()))));
            for (MessageEmbed.Field field : fieldOverflow) {
                ebOverflow.addField(field);
            }
            event.getHook().sendMessageEmbeds(eb.build(), ebOverflow.build()).queue();
        } else event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private static int getWordCount(EmbedBuilder eb, int wordCount, String returnedWord, List<MessageEmbed.Field> fieldOverflow, @NotNull Result result) {
        for (LexicalEntry lexicalEntry : result.getLexicalEntries()) {
            for (Entry entry : lexicalEntry.getEntries()) {
                for (Sense sens : entry.getSenses()) {
                    String fieldTitle;
                    for (String definition : sens.getDefinitions()) {
                        fieldTitle = "%s — *%s*.".formatted(WordUtils.capitalize(returnedWord), lexicalEntry.getLexicalCategory().getText());
                        if (wordCount < 25) {
                            eb.addField(fieldTitle, "```%s```".formatted(definition), false);
                        } else {
                            fieldOverflow.add(new MessageEmbed.Field(fieldTitle, "```%s```".formatted(definition), false));
                        }
                        wordCount++;
                    }
                    if (!sens.getSubsenses().isEmpty()) {
                        for (Subsense subsets : sens.getSubsenses()) {
                            for (String subsetsDefinition : subsets.getDefinitions()) {
                                fieldTitle = "˪ %s — *%s*."
                                    .formatted(WordUtils.capitalize(returnedWord), lexicalEntry.getLexicalCategory().getText());
                                if (wordCount < 25) {
                                    eb.addField(fieldTitle, "```%s```".formatted(subsetsDefinition), false);
                                } else {
                                    fieldOverflow.add(new MessageEmbed.Field(fieldTitle, "```%s```".formatted(subsetsDefinition), false));
                                }
                                wordCount++;
                            }
                        }
                    }
                }
            }
        }
        return wordCount;
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Look up a word in the dictionary in up to 9 different languages.", """
            Powered by Oxford Languages, this command returns all definitions of a given word in up to %d languages as long as it is within that language's lexicon.
                        
            WARNING: depending on the word it may return no definitions. Try a different variation of that word if it happens.
            """.formatted(langCodes.size()),
            Mastermind.DEVELOPER,
            SlashModule.FUN,
            format.parse("27-10-2022_12:46"),
            format.parse("1-12-2022_12:37")
        );

        metadata.addOptions(
            new OptionData(OptionType.STRING, "word", "The word to look up.", true),
            new OptionData(OptionType.STRING, "language", "The source language to look up. US English is default.", false)
                .addChoices(langChoices)
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        String[] userWords = event.getOption("word", "", OptionMapping::getAsString).split(" ");
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Dictionary")
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setImage("https://languages.oup.com/wp-content/uploads/ol-logo-colour-300px-sfw.jpg")
            .setColor(EmbedColors.getDefault());
        EmbedBuilder ebOverflow = new EmbedBuilder()
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setImage("https://languages.oup.com/wp-content/uploads/ol-logo-colour-300px-sfw.jpg")
            .setColor(EmbedColors.getSecondary());
        if (userWords.length > 1) {
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Only one word can be looked up at one time. Please try again.")
                    .build()
            ).queue();
            return;
        }
        if (userWords[0].equals(""))
            throw new DiscordAPIException("Required option [%s] was not given".formatted("word"));
        Map.Entry<String, String> language = langCodes.floorEntry(event.getOption("language", "US English", OptionMapping::getAsString));
        String toLookUp = userWords[0].toLowerCase(Locale.forLanguageTag(language.getValue()));

        URL dictionary = new URL("https://od-api.oxforddictionaries.com/api/v2/entries/%s/%s?fields=definitions&strictMatch=false".formatted(language.getValue(), toLookUp));
        HttpURLConnection oxfordConnection = (HttpURLConnection) dictionary.openConnection();
        oxfordConnection.addRequestProperty("Accept", "application/json");
        oxfordConnection.addRequestProperty("app_id", Config.getConfig().get("OXFORD_ID"));
        oxfordConnection.addRequestProperty("app_key", Config.getConfig().get("OXFORD_KEY"));
        int responseCode = oxfordConnection.getResponseCode();
        ObjectMapper om = new ObjectMapper();

        switch (responseCode) {
            case 200 -> run200(event, eb, ebOverflow, language, toLookUp, oxfordConnection, om);
            case 400 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("Unable to get the definition of [%s]. Make sure you have typed the word correctly, If this message continues to appear, please contact <@%s> to fix this."
                            .formatted(toLookUp.toUpperCase(Locale.forLanguageTag(language.getValue())), Config.getConfig().get("OWNER_ID"))
                        )
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("400 Bad Request: %s".formatted(message));
            }
            case 403 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("403 Authentication failed: %s".formatted(message));
            }
            case 404 -> event.getHook().sendMessageEmbeds(
                eb.setTitle("Definition - %s".formatted(toLookUp.toUpperCase(Locale.forLanguageTag(language.getValue()))))
                    .setDescription("There are no *%s* definitions for [%s].\n\nEither the word does not exist or you have mistyped.".formatted(language.getKey(), toLookUp.toUpperCase(Locale.forLanguageTag(language.getValue()))))
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            case 500 -> {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("Looks like something went wrong with the API. Please let <@%s> know so he can send a message to Oxford."
                            .formatted(Config.getConfig().get("OWNER_ID")))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();

                String message = om.readValue(oxfordConnection.getInputStream(), OxfordError.class).getError();
                log.error("500 Internal Server Error: %s".formatted(message));
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
                log.error("502 Bad Gateway: %s".formatted(message));
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
                log.error("%s %s: %s".formatted(responseCode, isIt503 ? "Service Unavailable" : "Gateway timeout", message));
            }
        }

        oxfordConnection.disconnect();
    }
}
