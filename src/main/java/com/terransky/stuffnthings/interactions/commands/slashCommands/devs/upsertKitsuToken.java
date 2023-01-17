package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

@SuppressWarnings("SpellCheckingInspection")
public class upsertKitsuToken implements ICommandSlash {

    @Override
    public String getName() {
        return "upsert-kitsu-token";
    }

    @Override
    public boolean isDeveloperCommand() {
        return !ICommandSlash.super.isDeveloperCommand();
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(getName(), "Upsert the Authorization token for `Kitsu.io`.",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            format.parse("16-1-2023_13:00"),
            format.parse("17-1-2023_11:48")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        boolean isObtained = KitsuHandler.upsertAuthorizationToken();
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        if (!isObtained) {
            event.replyEmbeds(
                builder.setColor(EmbedColors.getError())
                    .setDescription("Unable to upsert token. Please check logs for details.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(
            builder.setColor(EmbedColors.getDefault())
                .setDescription(String.format("Token has been upserted. Saved into `%s` on bot root directory.", KitsuHandler.FILE_NAME))
                .build()
        ).setEphemeral(true).queue();
    }
}
