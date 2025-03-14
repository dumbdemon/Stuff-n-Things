package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.tinyURL.*;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.TinyURLHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class TinyURL implements ICommandSlash {
    private static void validationFailed(@NotNull SlashCommandInteractionEvent event, String url, EmbedBuilder builder,
                                         @NotNull TinyURLResponse shortURLData) {
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
                .setColor(EmbedColor.ERROR.getColor())
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
        return !StuffNThings.getConfig().getTokens().getTinyUrl().getToken().isEmpty();
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Create short URLs with TinyURL",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            Metadata.parseDate(2023, 1, 6, 16, 4),
            Metadata.parseDate(2024, 8, 20, 12, 3)
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
    public boolean isWorking() {
        return isDeveloperCommand();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        Optional<String> ifUrl = Optional.ofNullable(event.getOption("url", OptionMapping::getAsString)),
            alias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        TinyURLLimits.Domain domain = TinyURLLimits.Domain.getDomainByKey(event.getOption("domain", 0, OptionMapping::getAsInt));

        String url = ifUrl.orElseThrow(DiscordAPIException::new);
        EmbedBuilder embedBuilder = blob.getStandardEmbed(getNameReadable());

        try {
            TinyURLForm tinyURLForm = new TinyURLForm(url)
                .withDomain(domain);
            alias.ifPresent(tinyURLForm::withAlias);

            TinyURLHandler tinyURLHandler = new TinyURLHandler();
            TinyURLResponse response = tinyURLHandler.sendRequest(tinyURLForm);
            String requestData = tinyURLForm.getAsJsonString(),
                reportingURL = StuffNThings.getConfig().getCore().getReportingUrl();

            if (StuffNThings.getConfig().getCore().getTestingMode())
                embedBuilder.setDescription(String.format("Data Packet Sent%n```json%n%s%n```", requestData));

            switch ((int) (long) response.getCode()) {
                case 0 -> {
                    UrlData urlData = ((ValidTinyURLResponse) response).getData();
                    OffsetDateTime createdAt = urlData.getCreatedAtAsDate();
                    OffsetDateTime expiresAt = urlData.getExpiresAtAsDate();

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
                            response.getCode() == 7 ? "server operations. Please try again in a few moments" :
                                String.format("authorization. Please report it [here](%s)", reportingURL)))
                        .setColor(EmbedColor.ERROR.getColor())
                        .build()
                ).queue();
                case 3 -> event.getHook().sendMessageEmbeds(
                    embedBuilder.appendDescription(
                            String.format("Unable to access TinyURL servers. If this continues, please report this [here](%s).",
                                reportingURL))
                        .setColor(EmbedColor.ERROR.getColor())
                        .build()
                ).queue();
                case 5 -> validationFailed(event, url, embedBuilder, response);
                default -> event.getHook().sendMessageEmbeds(
                    embedBuilder.appendDescription(String.format("An unknown operation occurred on bot side. Please report it [here](%s).",
                            reportingURL))
                        .setColor(EmbedColor.ERROR.getColor())
                        .build()
                ).queue();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            event.getHook().sendMessageEmbeds(
                embedBuilder.setDescription("""
                        URL is not valid.
                        Please verify that the URL is correct and try again.
                        
                        Note: Valid URls start with `http://` or `https://` and must end with a domain such as `.com`, `.gov`, `.xyz`, etc.
                        """)
                    .setColor(EmbedColor.ERROR.getColor())
                    .addField("URL Given", String.format("```%n%s%n```", url), false)
                    .build()
            ).queue();
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("error during network operation", e);
            event.getHook().sendMessageEmbeds(
                embedBuilder.setDescription(Responses.NETWORK_OPERATION.getMessage())
                    .setColor(EmbedColor.ERROR.getColor())
                    .build()
            ).queue();
        }
    }
}
