package com.terransky.StuffnThings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import com.terransky.StuffnThings.jacksonMapper.owlBotData.Definition;
import com.terransky.StuffnThings.jacksonMapper.owlBotData.OwlBotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class dictionary implements ISlashCommand {
    @Override
    public String getName() {
        return "dictionary";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Look up a word in the English dictionary.")
            .addOptions(
                new OptionData(OptionType.STRING, "word", "The word to look up.", true)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        String toLookUp = event.getOption("word", "", OptionMapping::getAsString);
        if (toLookUp.equals("")) throw new DiscordAPIException("Required option [%s] was not given".formatted("word"));
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Dictionary")
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .setColor(Commons.defaultEmbedColor);

        URL owlBot = new URL("https://owlbot.info/api/v4/dictionary/%s?format=json".formatted(toLookUp));
        HttpURLConnection owlBotConnection = (HttpURLConnection) owlBot.openConnection();
        owlBotConnection.addRequestProperty("Authorization", "Token %s".formatted(Commons.config.get("OWL_BOT_TOKEN")));

        switch (owlBotConnection.getResponseCode()) {
            case 200 -> {
                ObjectMapper om = new ObjectMapper();

                OwlBotData definitions = om.readValue(new InputStreamReader(owlBotConnection.getInputStream()), OwlBotData.class);

                List<MessageEmbed> defEmbedList = new ArrayList<>();
                defEmbedList.add(
                    eb.setDescription("Here are all the definitions for [%s]".formatted(toLookUp.toUpperCase()))
                        .build()
                );
                int i = 1;

                for (Definition definition : definitions.getDefinitions()) {
                    defEmbedList.add(
                        new EmbedBuilder()
                            .addField("Type", WordUtils.capitalize(definition.getType()), false)
                            .addField("Definition", WordUtils.capitalize(definition.getDefinition(), '.'), false)
                            .addField("Example", (definition.getExample() != null) ? WordUtils.capitalize(definition.getExample(), '.') : "*No example given.*", false)
                            .setFooter("Definition #%d".formatted(i), "https://owlbot.info/static/owlbot/img/logo.png")
                            .setColor(Commons.secondaryEmbedColor)
                            .build()
                    );
                    i++;
                }

                owlBotConnection.disconnect();
                event.replyEmbeds(defEmbedList).queue();
            }
            case 401, 429 -> event.replyEmbeds(
                eb.setDescription("Unable to get the definition of [%s]. Please wait a couple minutes and try again. If this message continues to appear, please contact <@%s> to fis this.".formatted(toLookUp.toUpperCase(), Commons.config.get("OWNER_ID")))
                    .build()
            ).queue();
            case 404 -> event.replyEmbeds(
                eb.setDescription("There are no definitions for [%s]. Either the word does not exist or you have mistyped.".formatted(toLookUp.toUpperCase()))
                    .build()
            ).queue();
        }
    }
}
