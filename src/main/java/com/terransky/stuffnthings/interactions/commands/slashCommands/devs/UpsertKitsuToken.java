package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class UpsertKitsuToken implements ICommandSlash {

    @Override
    public String getName() {
        return "upsert-kitsu-token";
    }

    @Override
    public boolean isDeveloperCommand() {
        return !ICommandSlash.super.isDeveloperCommand();
    }

    @Override
    public List<Long> getServerRestrictions() {
        return List.of(Config.getSupportGuildIdLong());
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Upsert the Authorization token for `Kitsu.io`.",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            Metadata.parseDate("2023-01-16T13:00Z"),
            Metadata.parseDate("2023-02-27T16:17Z")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        boolean isObtained = KitsuHandler.upsertAuthorizationToken();
        EmbedBuilder builder = blob.getStandardEmbed(getNameReadable());

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
