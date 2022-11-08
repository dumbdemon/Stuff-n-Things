package com.terransky.StuffnThings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.dataSources.oxfordDictionary.*;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Look up a word in the dictionary in up to 9 different languages.")
            .addOptions(
                new OptionData(OptionType.STRING, "word", "The word to look up.", true),
                new OptionData(OptionType.STRING, "language", "The source language to look up. US English is default.", false)
                    .addChoices(langChoices)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        String[] userWords = event.getOption("word", "", OptionMapping::getAsString).split("\s");
        EmbedBuilder eb1 = new EmbedBuilder()
            .setTitle("Dictionary")
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .setImage("https://languages.oup.com/wp-content/uploads/ol-logo-colour-300px-sfw.jpg")
            .setColor(Commons.DEFAULT_EMBED_COLOR);
        EmbedBuilder eb2 = new EmbedBuilder()
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .setImage("https://languages.oup.com/wp-content/uploads/ol-logo-colour-300px-sfw.jpg")
            .setColor(Commons.SECONDARY_EMBED_COLOR);
        if (userWords.length > 1) {
            event.getHook().sendMessageEmbeds(
                eb1.setDescription("Only one word can be looked up at one time. Please try again.")
                    .build()
            ).queue();
            return;
        }
        if (userWords[0].equals(""))
            throw new DiscordAPIException("Required option [%s] was not given".formatted("word"));
        Map.Entry<String, String> e = langCodes.floorEntry(event.getOption("language", "US English", OptionMapping::getAsString));
        String toLookUp = userWords[0].toLowerCase(Locale.forLanguageTag(e.getValue()));

        URL dictionary = new URL("https://od-api.oxforddictionaries.com/api/v2/entries/%s/%s?fields=definitions&strictMatch=false".formatted(e.getValue(), toLookUp));
        HttpURLConnection oxfordConnection = (HttpURLConnection) dictionary.openConnection();
        oxfordConnection.addRequestProperty("Accept", "application/json");
        oxfordConnection.addRequestProperty("app_id", Commons.CONFIG.get("OXFORD_ID"));
        oxfordConnection.addRequestProperty("app_key", Commons.CONFIG.get("OXFORD_KEY"));
        int responseCode = oxfordConnection.getResponseCode();
        ObjectMapper om = new ObjectMapper();

        switch (responseCode) {
            case 200 -> {
                OxfordData oxfordData = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordData.class);
                int i = 0;
                String returnedWord = "";
                List<MessageEmbed.Field> fieldOverflow = new ArrayList<>();

                for (Result result : oxfordData.getResults()) {
                    returnedWord = result.getWord();
                    if (returnedWord.equalsIgnoreCase(toLookUp)) {
                        for (LexicalEntry lexicalEntry : result.getLexicalEntries()) {
                            for (Entry entry : lexicalEntry.getEntries()) {
                                for (Sense sens : entry.getSenses()) {
                                    String fieldTitle;
                                    for (String definition : sens.getDefinitions()) {
                                        fieldTitle = "%s \u2014 *%s*.".formatted(WordUtils.capitalize(returnedWord), lexicalEntry.getLexicalCategory().getText());
                                        if (i < 25) {
                                            eb1.addField(fieldTitle, "```%s```".formatted(definition), false);
                                        } else {
                                            fieldOverflow.add(new MessageEmbed.Field(fieldTitle, "```%s```".formatted(definition), false));
                                        }
                                        i++;
                                    }
                                    if (sens.getSubsenses().size() != 0) {
                                        for (Subsense subsets : sens.getSubsenses()) {
                                            for (String subsetsDefinition : subsets.getDefinitions()) {
                                                fieldTitle = "\u02EA %s \u2014 *%s*."
                                                    .formatted(WordUtils.capitalize(returnedWord), lexicalEntry.getLexicalCategory().getText());
                                                if (i < 25) {
                                                    eb1.addField(fieldTitle, "```%s```".formatted(subsetsDefinition), false);
                                                } else {
                                                    fieldOverflow.add(new MessageEmbed.Field(fieldTitle, "```%s```".formatted(subsetsDefinition), false));
                                                }
                                                i++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (i == 0) {
                    event.getHook().sendMessageEmbeds(
                        eb1.setTitle("Definition - %s".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(e.getValue()))))
                            .setDescription("%s is in the dictionary; however, there appears to be no definitions. Try using a different variation of the word."
                                .formatted(WordUtils.capitalize(returnedWord))
                            ).build()
                    ).queue();
                    return;
                }

                boolean moreThanOne = i > 1;

                eb1.setTitle("Definition - %s".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(e.getValue()))))
                    .setDescription("There %s %d *%s* definition%s for *%s*.%s"
                        .formatted(
                            moreThanOne ? "are" : "is",
                            i,
                            e.getKey(),
                            moreThanOne ? "s" : "",
                            returnedWord,
                            e.getKey().endsWith("English") ? "" : "\n\n**Warning**: You are searching in a language other than English. Certain characters may not show if your browser/device does not support it."
                        )
                    );

                if (fieldOverflow.size() > 0) {
                    eb2.setTitle("Definition - %s cont.".formatted(returnedWord.toUpperCase(Locale.forLanguageTag(e.getValue()))));
                    for (MessageEmbed.Field field : fieldOverflow) {
                        eb2.addField(field);
                    }
                    event.getHook().sendMessageEmbeds(eb1.build(), eb2.build()).queue();
                } else event.getHook().sendMessageEmbeds(eb1.build()).queue();
            }
            case 400 -> {
                event.getHook().sendMessageEmbeds(
                    eb1.setDescription("Unable to get the definition of [%s]. Make sure you have typed the word correctly, If this message continues to appear, please contact <@%s> to fix this."
                        .formatted(toLookUp.toUpperCase(Locale.forLanguageTag(e.getValue())), Commons.CONFIG.get("OWNER_ID"))
                    ).build()
                ).queue();

                String message = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordError.class).getError();
                log.error("400 Bad Request: %s".formatted(message));
            }
            case 403 -> {
                event.getHook().sendMessageEmbeds(
                    eb1.setDescription("")
                        .build()
                ).queue();

                String message = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordError.class).getError();
                log.error("403 Authentication failed: %s".formatted(message));
            }
            case 404 -> event.getHook().sendMessageEmbeds(
                eb1.setTitle("Definition - %s".formatted(toLookUp.toUpperCase(Locale.forLanguageTag(e.getValue()))))
                    .setDescription("There are no *%s* definitions for [%s].\n\nEither the word does not exist or you have mistyped.".formatted(e.getKey(), toLookUp.toUpperCase(Locale.forLanguageTag(e.getValue()))))
                    .build()
            ).queue();
            case 500 -> {
                event.getHook().sendMessageEmbeds(
                    eb1.setDescription("Looks like something went wrong with the API. Please let <@%s> know so he can send a message to Oxford."
                            .formatted(Commons.CONFIG.get("OWNER_ID")))
                        .build()
                ).queue();

                String message = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordError.class).getError();
                log.error("500 Internal Server Error: %s".formatted(message));
            }
            case 414 -> event.getHook().sendMessageEmbeds(
                eb1.setDescription("Your word can be at most 128 characters. Please look up a different word.")
                    .build()
            ).queue();
            case 502 -> {
                event.getHook().sendMessageEmbeds(
                    eb1.setDescription("The API server is currently down or being upgraded. Please try this command later.\n\n[Click me to check.](https://downforeveryoneorjustme.com/languages.oup.com?proto=https)")
                        .build()
                ).queue();

                String message = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordError.class).getError();
                log.error("502 Bad Gateway: %s".formatted(message));
            }
            case 503, 504 -> {
                boolean isIt503 = responseCode == 503;
                String serviceUnavailable = "there appear to be too many requests in the queue";
                String gatewayTimeout = "something went wrong while in queue";
                event.getHook().sendMessageEmbeds(
                    eb1.setDescription("The API servers are up, but %s. Please wait a couple moments and try again.".formatted(isIt503 ? serviceUnavailable : gatewayTimeout))
                        .build()
                ).queue();

                String message = om.readValue(new InputStreamReader(oxfordConnection.getInputStream()), OxfordError.class).getError();
                log.error("%s %s: %s".formatted(responseCode, isIt503 ? "Service Unavailable" : "Gateway timeout", message));
            }
        }

        oxfordConnection.disconnect();
    }
}
