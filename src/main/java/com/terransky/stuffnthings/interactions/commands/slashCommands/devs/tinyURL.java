package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.dataSources.tinyURL.Data;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLRequestBuilder;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import com.terransky.stuffnthings.utilities.general.TinyURLHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class tinyURL implements ICommandSlash {
    private static void oops(@NotNull SlashCommandInteractionEvent event, String url, String requestData,
                             EmbedBuilder builder, @NotNull TinyURLData shortURLData) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> errors = shortURLData.getErrors();
        for (String error : errors) {
            stringBuilder.append(errors.indexOf(error) + 1)
                .append(" >> ")
                .append(error)
                .append("\n");
        }
        if (Config.isTestingMode())
            builder.setDescription(String.format("Sent Packet: ```json\n%s\n```\n", requestData));
        event.getHook().sendMessageEmbeds(
            builder
                .appendDescription(String.format("Unable to shorten [URL](%s) for the following reason%s:\n```%s```",
                    url,
                    errors.size() > 1 ? "s" : "",
                    stringBuilder))
                .setColor(EmbedColors.getError())
                .build()
        ).queue();
    }

    @Override
    public String getName() {
        return "tinyurl";
    }

    /**
     * Production bot requires that the devs have <a href="https://tinyurl.com/app/pricing">TinyURL Pro</a> for this command due
     * to potentially hitting the API limit of 600 per month.
     */
    @Override
    public boolean isDeveloperCommand() {
        return true;
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(getName(), "Create short URLs with TinyURL", """
            Create short URLs with TinyURL.
            """, Mastermind.DEVELOPER, SlashModule.DEVS,
            format.parse("6-1-2023_16:04"),
            new Date()
        )
            .addOptions(
                new OptionData(OptionType.STRING, "url", "A URL to shorten.", true),
                new OptionData(OptionType.STRING, "alias", "Customize the link.")
                    .setRequiredLength(TinyURLRequestBuilder.Lengths.ALIAS.getMin(),
                        TinyURLRequestBuilder.Lengths.ALIAS.getMax())
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        Optional<String> ifUrl = Optional.ofNullable(event.getOption("url", OptionMapping::getAsString)),
            alias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        String url = ifUrl.orElseThrow(DiscordAPIException::new);
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        TinyURLRequestBuilder requestBuilder = new TinyURLRequestBuilder(url);
        alias.ifPresent(requestBuilder::withAlias);

        TinyURLHandler tinyURLHandler = new TinyURLHandler(requestBuilder);
        TinyURLData shortURLData = tinyURLHandler.sendRequest();
        String requestData = tinyURLHandler.getRequestBody();

        if (shortURLData.getCode() != 0) {
            oops(event, url, requestData, builder, shortURLData);
            return;
        }

        Data urlData = shortURLData.getData();
        String shortenURL = urlData.getTinyUrl();
        Date createdAt = urlData.getCreatedAt();
        Date expiresAt = urlData.getExpiresAt();

        if (Config.isTestingMode())
            builder.setDescription(String.format("Data Packet Sent\n```json\n%s\n```", requestData));

        event.getHook().sendMessageEmbeds(
            builder.addField("Long URL", url, false)
                .addField("Shorten URL", shortenURL, false)
                .addField("Created", Timestamp.getDateAsTimestamp(createdAt, Timestamp.LONG_DATE_W_SHORT_TIME), false)
                .addField("Expires", expiresAt != null ? Timestamp.getDateAsTimestamp(expiresAt, Timestamp.LONG_DATE_W_SHORT_TIME) :
                    "Never", false)
                .build()
        ).queue();
    }
}
