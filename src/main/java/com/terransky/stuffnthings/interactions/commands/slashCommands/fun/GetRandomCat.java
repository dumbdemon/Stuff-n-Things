package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.catAAS.CatAASData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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
import java.util.ArrayList;
import java.util.Optional;

public class GetRandomCat extends SlashCommandInteraction {
    public GetRandomCat() {
        super("random-cat", "Get a random image of a cat",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 9, 13, 17),
            parseDate(2025, 12, 28, 0, 18)
        );
        addOptions(
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

    @NotNull
    private Container getMessageEmbed(@NotNull CatAASData catAASData) {
        return StandardResponse.getResponseContainer(
            this,
            TextDisplay.ofFormat("I couldn't get a cat because: `%s`\nTry using different options?", catAASData.getMessage()),
            BotColors.ERROR
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        boolean sendGifs = event.getOption("gif", false, OptionMapping::getAsBoolean);
        Optional<String> says = Optional.ofNullable(event.getOption("says", OptionMapping::getAsString));
        Optional<String> filter = Optional.ofNullable(event.getOption("filter", OptionMapping::getAsString));
        String baseURL = "https://cataas.com";
        ObjectMapper mapper = new ObjectMapper();

        String url = baseURL + "/c" +
            (sendGifs ? "/gif" : "") +
            says.map(s -> String.format("/s/%s?", s)).orElse("?") +
            filter.map(s -> sendGifs ? "" : String.format("fi=%s", s)).orElse("") +
            (filter.isPresent() && !sendGifs ? "&" : "") + "json=true";

        CatAASData catAASData = mapper.readValue(new URL(url).openStream(), CatAASData.class);

        if (catAASData.hasError()) {
            event.getHook().sendMessageComponents(
                getMessageEmbed(catAASData)
            ).queue();
            return;
        }

        HttpURLConnection catAAS = (HttpURLConnection) new URL(baseURL + catAASData.getUrl()).openConnection();
        if (catAAS.getResponseCode() != 200) {
            CatAASData err = mapper.readValue(catAAS.getErrorStream(), CatAASData.class);
            event.getHook().sendMessageComponents(
                getMessageEmbed(err)
            ).queue();
            return;
        }

        event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(WordUtils.capitalize(catAASData.getEffectiveOwner()), new ArrayList<>() {{
                    if (sendGifs && filter.isPresent()) {
                        add(TextDisplay.of("***Cannot have gif and filter. Skipping filter parameter.***"));
                    }
                    add(TextDisplay.ofFormat("Created at %s", catAASData.getCreatedAtAsTimestamp()));
                    add(TextDisplay.ofFormat("Last Updated on %s", catAASData.getUpdatedAtAsTimestamp()));
                    add(TextDisplay.ofFormat("Tag%s\n%s", catAASData.getTags().size() > 1 ? "s" : "", catAASData.getTagsAsString()));
                    add(Separator.createDivider(Separator.Spacing.SMALL));
                    add(MediaGallery.of(MediaGalleryItem.fromFile(FileUpload.fromData(catAAS.getInputStream(), catAASData.getFile()))));
                }})
            )
            .queue();
    }
}
