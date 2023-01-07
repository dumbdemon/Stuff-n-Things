package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.tinyURL.Data;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLNoData;
import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLRequestBuilder;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tinyURL implements ICommandSlash {
    private static void oops(@NotNull SlashCommandInteractionEvent event, String url, String requestData,
                             EmbedBuilder builder, @NotNull HttpResponse<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder stringBuilder = new StringBuilder();
        TinyURLNoData shortURLNoData = mapper.readValue(response.body(), TinyURLNoData.class);
        List<String> errors = shortURLNoData.getErrors();
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

    @Override
    public boolean isWorking() {
        return Config.isTestingMode();
    }

    /**
     * Production bot requires that the devs have <a href="https://tinyurl.com/app/pricing">TinyURL Pro</a> for this command due
     * to potentially hitting the API limit of 600 per month.
     */
    @Override
    public boolean isDeveloperCommand() {
        return false;
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
        Optional<String> ifUrl = Optional.ofNullable(event.getOption("url", OptionMapping::getAsString)),
            alias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        String url = ifUrl.orElseThrow(DiscordAPIException::new),
            requestData;
        ObjectMapper mapper = new ObjectMapper();
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setColor(EmbedColors.getError())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        try {
            var requestDataBuilder = new TinyURLRequestBuilder(url)
                .withDomain(TinyURLRequestBuilder.Domains.ONE)
                .withTags("");
            alias.ifPresent(requestDataBuilder::withAlias);
            requestData = requestDataBuilder.build();
        } catch (MalformedURLException | URISyntaxException ignored) {
            event.replyEmbeds(builder
                .setDescription("You have entered an invalid URL. Please verify what was given and try again.")
                .addField("Given", url, false)
                .setColor(EmbedColors.getError())
                .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        ExecutorService service = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(service)
            .build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tinyurl.com/create?api_token=" + Config.getTinyURLToken()))
            .setHeader("accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestData))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        TinyURLData shortURLData = null;

        try {
            shortURLData = mapper.readValue(response.body(), TinyURLData.class);
        } catch (Exception ignored) {
            oops(event, url, requestData, builder, response);
        } finally {
            service.shutdownNow();
            System.gc();
        }
        if (shortURLData == null) return;

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
