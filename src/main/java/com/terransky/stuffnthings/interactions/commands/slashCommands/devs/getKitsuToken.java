package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
public class getKitsuToken implements ICommandSlash {
    @Override
    public String getName() {
        return "get-kitsu-token";
    }

    @Override
    public boolean isWorking() {
        return Config.getKitsuToken().equals("") || Config.isTestingMode();
    }

    @Override
    public boolean isDeveloperCommand() {
        return !ICommandSlash.super.isDeveloperCommand();
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(getName(), "Obtain an Authorization token for `Kitsu.io`.",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            format.parse("16-1-2023_13:00"),
            new Date()
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        boolean isObtained = KitsuHandler.getAuthorizationToken();
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        if (!isObtained) {
            event.replyEmbeds(
                builder.setColor(EmbedColors.getError())
                    .setDescription("Unable to obtain token. Please check logs for details.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(
            builder.setColor(EmbedColors.getDefault())
                .setDescription("Token obtained. Saved into `kitsuToken.txt` on bot root directory.")
                .build()
        ).setEphemeral(true).queue();
    }
}
