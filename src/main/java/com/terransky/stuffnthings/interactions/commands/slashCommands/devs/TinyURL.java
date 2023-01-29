package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.dataSources.tinyURL.Data;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLLimits;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.TinyURLHandler;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TinyURL implements ICommandSlash {
    private static void validationFailed(@NotNull SlashCommandInteractionEvent event, String url, EmbedBuilder builder,
                                         @NotNull TinyURLData shortURLData) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> errors = shortURLData.getErrors();
        for (String error : errors) {
            stringBuilder.append(errors.indexOf(error) + 1)
                .append(" >> ")
                .append(error)
                .append("\n");
        }
        event.getHook().sendMessageEmbeds(
            builder
                .appendDescription(String.format("Unable to shorten [URL](%s) for the following reason%s:%n```%s```",
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
        return !Config.getTinyURlDomain().equals("");
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(getName(), "Create short URLs with TinyURL",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            format.parse("6-1-2023_16:04"),
            format.parse("15-1-2023_17:37")
        )
            .addOptions(
                new OptionData(OptionType.STRING, "url", "A URL to shorten.", true),
                new OptionData(OptionType.STRING, "alias", "Customize the link.")
                    .setRequiredLength(TinyURLLimits.Lengths.ALIAS.getMin(),
                        TinyURLLimits.Lengths.ALIAS.getMax()),
                new OptionData(OptionType.STRING, "domain", "Choose a custom Domain")
                    .addChoices(TinyURLLimits.Domain.getDomainsAsChoices())
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        Optional<String> ifUrl = Optional.ofNullable(event.getOption("url", OptionMapping::getAsString)),
            alias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        TinyURLLimits.Domain domain = TinyURLLimits.Domain.getDomainByKey(event.getOption("domain", 0, OptionMapping::getAsInt));

        String url = ifUrl.orElseThrow(DiscordAPIException::new);
        EmbedBuilder embedBuilder = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        try {
            TinyURLHandler tinyURLHandler = new TinyURLHandler(url);
            tinyURLHandler.withDomain(domain);
            alias.ifPresent(tinyURLHandler::withAlias);
            TinyURLData shortURLData = tinyURLHandler.sendRequest();
            String requestData = tinyURLHandler.getRequestBody(),
                reportingURL = Config.getErrorReportingURL();

            if (Config.isTestingMode())
                embedBuilder.setDescription(String.format("Data Packet Sent%n```json%n%s%n```", requestData));

            switch (shortURLData.getCode()) {
                case 0 -> {
                    Data urlData = shortURLData.getData();
                    Date createdAt = urlData.getCreatedAt();
                    Date expiresAt = urlData.getExpiresAt();

                    event.getHook().sendMessageEmbeds(
                        embedBuilder.addField("Long URL", url, false)
                            .addField("Shorten URL", urlData.getTinyUrl(), false)
                            .addField("Created", Timestamp.getDateAsTimestamp(createdAt, Timestamp.LONG_DATE_W_SHORT_TIME), false)
                            .addField("Expires", expiresAt != null ?
                                Timestamp.getDateAsTimestamp(expiresAt, Timestamp.LONG_DATE_W_SHORT_TIME) : "Never", false)
                            .build()
                    ).queue();
                }
                case 1, 4, 7 -> event.getHook().sendMessageEmbeds(
                    embedBuilder.appendDescription(String.format("An error occurred during %s.",
                            shortURLData.getCode() == 7 ? "server operations. Please try again in a few moments" :
                                String.format("authorization. Please report it [here](%s)", reportingURL)))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
                case 3 -> event.getHook().sendMessageEmbeds(
                    embedBuilder.appendDescription(
                            String.format("Unable to access TinyURL servers. If this continues, please report this [here](%s).",
                                reportingURL))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
                case 5 -> validationFailed(event, url, embedBuilder, shortURLData);
                default -> event.getHook().sendMessageEmbeds(
                    embedBuilder.appendDescription(String.format("An unknown operation occurred on bot side. Please report it [here](%s).",
                            reportingURL))
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
            }
        } catch (MalformedURLException | URISyntaxException ignored) {
            event.getHook().sendMessageEmbeds(
                embedBuilder.setDescription("""
                        URL is not valid.
                        Please verify that the URL is correct and try again.
                                            
                        Note: Valid URls start with `http://` or `https://` and must end with a domain such as `.com`, `.gov`, `.xyz`, etc.
                        """)
                    .addField("URL Given", String.format("```%n%s%n```", url), false)
                    .build()
            ).queue();
        }
    }
}
