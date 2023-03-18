package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class Lmgtfy implements ICommandSlash {
    @Override
    public String getName() {
        return "lmgtfy";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Let me Google that for you!", """
            When a person is too lazy to search it up themselves, call this on 'em.
            """, Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate("2022-08-24T11:10Z"),
            Metadata.parseDate("2023-03-18T16:35Z")
        )
            .addSubcommands(
                new SubcommandData("web", "Let me Google that for you!")
                    .addOptions(
                        new OptionData(OptionType.STRING, "search", "What to search for.", true),
                        new OptionData(OptionType.USER, "victim", "Ping this person to victimize them!")
                    ),
                new SubcommandData("images", "Let me Google an image for you!")
                    .addOptions(
                        new OptionData(OptionType.STRING, "search", "What to search for.", true),
                        new OptionData(OptionType.USER, "victim", "Ping this person to victimize them!")
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String toSearch = event.getOption("search", "", OptionMapping::getAsString);
        String searchURL = "https://lmgtfy.app/?q=" +
            URLEncoder.encode(toSearch, StandardCharsets.UTF_8) +
            ("images".equals(event.getSubcommandName()) ? "&t=i" : "");
        User victim = event.getOption("victim", OptionMapping::getAsUser);
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle("LMGTFY [" + WordUtils.capitalize(toSearch) + "]", searchURL)
            .setDescription("For all those people who find it more convenient to bother " + event.getUser().getAsMention() + " with their question rather than to Google it for themselves.");

        if (victim != null) {
            User.Profile victimsProfile = victim.retrieveProfile().submit().get();
            Color embedColor = victimsProfile.getAccentColorRaw() == User.DEFAULT_ACCENT_COLOR_RAW ? Color.WHITE : victimsProfile.getAccentColor();
            builder.setColor(embedColor);
            event.reply(victim.getAsMention())
                .addEmbeds(builder.build()).queue();
        } else {
            builder.setColor(Color.WHITE);
            event.replyEmbeds(builder.build()).queue();
        }
    }
}
