package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.catAAS.CatAASData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class GetRandomCat implements ICommandSlash {
    @NotNull
    private static MessageEmbed getMessageEmbed(@NotNull EmbedBuilder response, @NotNull CatAASData catAASData) {
        return response.setColor(EmbedColor.ERROR.getColor())
            .setDescription(String.format("I couldn't get a cat because: `%s`", catAASData.getMessage()))
            .appendDescription("\nTry using different options?")
            .build();
    }

    @Override
    public String getName() {
        return "random-cat";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Get a random image of a cat",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-09T13:17Z"),
            Metadata.parseDate("2023-03-05T16:18Z")
        )
            .addOptions(
                new OptionData(OptionType.BOOLEAN, "gif", "Whether not you just want gifs."),
                new OptionData(OptionType.STRING, "says", "Add text to the image."),
                new OptionData(OptionType.STRING, "filter", "Add a filter to the image.")
                    .addChoices(
                        new Command.Choice("Blur", "blur"),
                        new Command.Choice("Monochrome", "mono"),
                        new Command.Choice("Sepia", "sepia"),
                        new Command.Choice("Negative", "negative"),
                        new Command.Choice("Paint", "paint"),
                        new Command.Choice("Pixel", "pixel")
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        boolean sendGifs = event.getOption("gif", false, OptionMapping::getAsBoolean);
        Optional<String> says = Optional.ofNullable(event.getOption("says", OptionMapping::getAsString));
        Optional<String> filter = Optional.ofNullable(event.getOption("filter", OptionMapping::getAsString));
        String baseURL = "https://cataas.com";
        EmbedBuilder response = blob.getStandardEmbed(getNameReadable());
        ObjectMapper mapper = new ObjectMapper();

        String url = baseURL + "/c" +
            (sendGifs ? "/gif" : "") +
            says.map(s -> String.format("/s/%s?", s)).orElse("?") +
            filter.map(s -> sendGifs ? "" : String.format("fi=%s", s)).orElse("") +
            (filter.isPresent() && !sendGifs ? "&" : "") + "json=true";

        CatAASData catAASData = mapper.readValue(new URL(url), CatAASData.class);

        if (catAASData.hasError()) {
            event.getHook().sendMessageEmbeds(
                getMessageEmbed(response, catAASData)
            ).queue();
            return;
        }

        HttpURLConnection catAAS = (HttpURLConnection) new URL(baseURL + catAASData.getUrl()).openConnection();
        if (catAAS.getResponseCode() != 200) {
            CatAASData err = mapper.readValue(catAAS.getErrorStream(), CatAASData.class);
            event.getHook().sendMessageEmbeds(
                getMessageEmbed(response, err)
            ).queue();
            return;
        }

        if (sendGifs && filter.isPresent()) {
            response.setDescription("***Cannot have gif and filter. Skipping filter parameter.***");
        }

        event.getHook().sendMessageEmbeds(
                response.setAuthor(WordUtils.capitalize(catAASData.getEffectiveOwner()))
                    .addField("Created at", catAASData.getCreatedAtAsTimestamp(), true)
                    .addField("Last Updated", catAASData.getUpdatedAtAsTimestamp(), true)
                    .addField("Tags", catAASData.getTagsAsString(), false)
                    .build()
            )
            .setFiles(FileUpload.fromData(catAAS.getInputStream(), catAASData.getFile()))
            .queue();
    }
}
