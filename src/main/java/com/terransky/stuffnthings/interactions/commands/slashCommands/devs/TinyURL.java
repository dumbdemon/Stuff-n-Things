package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.tinyURL.*;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.TinyURLHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TinyURL extends SlashCommandInteraction {

    public TinyURL() {
        super("tinyurl", "Create short URLs with TinyURL", Mastermind.DEVELOPER, CommandCategory.DEVS,
            parseDate(2023, 1, 6, 16, 4),
            parseDate(2025, 12, 27, 6, 31)
        );
        setDeveloperOnly(!StuffNThings.getConfig().getTokens().getTinyUrl().getToken().isEmpty());
        setWorking(isDeveloperOnly());
        addOptions(
            new OptionData(OptionType.STRING, "url", "A URL to shorten.", true),
            new OptionData(OptionType.STRING, "alias", "Customize the link.")
                .setRequiredLength(TinyURLLimits.Lengths.ALIAS.getMin(),
                    TinyURLLimits.Lengths.ALIAS.getMax()),
            new OptionData(OptionType.STRING, "domain", "Choose a custom Domain")
                .addChoices(TinyURLLimits.Domain.getDomainsAsChoices())
        );
    }

    private void validationFailed(@NotNull SlashCommandInteractionEvent event, String url,
                                  @NotNull TinyURLResponse shortURLData) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> errors = shortURLData.getErrors();
        for (String error : errors) {
            stringBuilder.append(errors.indexOf(error) + 1)
                .append(" >> ")
                .append(error)
                .append("\n");
        }
        event.getHook().sendMessageComponents(
            StandardResponse.getResponseContainer(this, String.format("Unable to shorten [URL](%s) for the following reason%s:%n```%s```",
                    url,
                    errors.size() > 1 ? "s" : "",
                    stringBuilder
                ),
                BotColors.ERROR
            )
        ).queue();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        Optional<String> ifUrl = Optional.ofNullable(event.getOption("url", OptionMapping::getAsString)),
            alias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        TinyURLLimits.Domain domain = TinyURLLimits.Domain.getDomainByKey(event.getOption("domain", 0, OptionMapping::getAsInt));

        String url = ifUrl.orElseThrow(DiscordAPIException::new);
        List<ContainerChildComponent> children = new ArrayList<>();

        try {
            TinyURLForm tinyURLForm = new TinyURLForm(url)
                .withDomain(domain);
            alias.ifPresent(tinyURLForm::withAlias);

            TinyURLHandler tinyURLHandler = new TinyURLHandler();
            TinyURLResponse response = tinyURLHandler.sendRequest(tinyURLForm);
            String requestData = tinyURLForm.getAsJsonString(),
                reportingURL = StuffNThings.getConfig().getCore().getReportingUrl();

            if (StuffNThings.getConfig().getCore().getTestingMode())
                children.add(TextDisplay.ofFormat("Data Packet Sent%n```json%n%s%n```", requestData));

            switch ((int) (long) response.getCode()) {
                case 0 -> {
                    UrlData urlData = ((ValidTinyURLResponse) response).getData();
                    OffsetDateTime createdAt = urlData.getCreatedAtAsDate();
                    OffsetDateTime expiresAt = urlData.getExpiresAtAsDate();

                    children.add(TextDisplay.ofFormat("### Long URL\n%s", url));
                    children.add(TextDisplay.ofFormat("### Shorten URL\n%s", urlData.getTinyUrl()));
                    children.add(TextDisplay.ofFormat("### Created on %s", Timestamp.getDateAsTimestamp(createdAt, Timestamp.LONG_DATE_W_SHORT_TIME)));
                    children.add(TextDisplay.ofFormat("### Expires %s", expiresAt != null ?
                        Timestamp.getDateAsTimestamp(expiresAt, Timestamp.LONG_DATE_W_SHORT_TIME) : "Never"));

                    event.getHook().sendMessageComponents(
                        StandardResponse.getResponseContainer(this, children)
                    ).queue();
                }
                case 1, 4, 7 -> event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer(this,
                        String.format("An error occurred during %s.",
                            response.getCode() == 7 ? "server operations. Please try again in a few moments" :
                                String.format("authorization. Please report it [here](%s)", reportingURL)), BotColors.ERROR)
                ).queue();
                case 3 -> event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer(this,
                        String.format("Unable to access TinyURL servers. If this continues, please report this [here](%s).", reportingURL),
                        BotColors.ERROR)
                ).queue();
                case 5 -> validationFailed(event, url, response);
                default -> event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer(this, String.format("An unknown operation occurred on bot side. Please report it [here](%s).",
                            reportingURL),
                        BotColors.ERROR)
                ).queue();
            }
        } catch (MalformedURLException | URISyntaxException e) {
            children.add(TextDisplay.of("""
                URL is not valid.
                Please verify that the URL is correct and try again.
                
                Note: Valid URls start with `http://` or `https://` and must end with a domain such as `.com`, `.gov`, `.xyz`, etc.
                """));
            children.add(TextDisplay.ofFormat("### URL Given\n%s", url));
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this, children, BotColors.ERROR)
            ).queue();
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("error during network operation", e);
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this, Responses.NETWORK_OPERATION)
            ).queue();
        }
    }
}
