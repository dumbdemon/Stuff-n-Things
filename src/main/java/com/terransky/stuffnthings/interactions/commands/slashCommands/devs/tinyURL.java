package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.dataSources.tinyURL.TinyURLRequestBuilder;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

public class tinyURL implements ICommandSlash {
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
            ifAlias = Optional.ofNullable(event.getOption("alias", OptionMapping::getAsString));
        String url = ifUrl.orElseThrow(DiscordAPIException::new),
            requestData;

        try {
            var requestDataBuilder = new TinyURLRequestBuilder(url)
                .withDomain(TinyURLRequestBuilder.Domains.ONE)
                .withTags("");
            ifAlias.ifPresent(requestDataBuilder::withAlias);
            requestData = requestDataBuilder.build();
        } catch (MalformedURLException | URISyntaxException ignored) {
            event.replyEmbeds(new EmbedBuilder()
                .setTitle(getNameReadable())
                .setDescription("You have entered an invalid URL. Please verify what was given and try again.")
                .addField("Given", url, false)
                .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                .setColor(EmbedColors.getError())
                .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        //todo: write GET request

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
            .setTitle(getNameReadable())
            .setDescription(String.format("Test Data Packet\n```json\n%s\n```", requestData))
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setColor(EmbedColors.getDefault())
            .build()
        ).queue();
    }
}
