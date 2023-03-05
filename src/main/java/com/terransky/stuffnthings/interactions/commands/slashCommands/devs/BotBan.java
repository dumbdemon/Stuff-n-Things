package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BotBan implements ICommandSlash {
    @Override
    public String getName() {
        return "bot-ban";
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        Optional<User> banned = Optional.ofNullable(event.getOption("user", OptionMapping::getAsUser));

        if (banned.isEmpty()) {
            event.replyEmbeds(
                blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
                    .setDescription("No user given.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        if (DatabaseManager.INSTANCE.botBan(banned.get(), false)) {
            event.replyEmbeds(
                blob.getStandardEmbed(getNameReadable(), EmbedColor.ERROR)
                    .setDescription("Couldn't ban " + banned.get().getAsTag() + ". Please check logs or try again.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(
            blob.getStandardEmbed(getNameReadable())
                .setDescription(banned.get().getAsTag() + " was bot banned.")
                .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Ban a user from ever using the bot again.",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-28T12:47Z"),
            Metadata.parseDate("2023-03-05T16:18Z")
        )
            .addOptions(
                new OptionData(OptionType.USER, "user", "This person does not deserve to use this bot...", true)
            );
    }

    @Override
    public boolean isDeveloperCommand() {
        return !ICommandSlash.super.isDeveloperCommand();
    }

    @Override
    public List<Long> getServerRestrictions() {
        return List.of(Config.getSupportGuildIdLong());
    }
}
