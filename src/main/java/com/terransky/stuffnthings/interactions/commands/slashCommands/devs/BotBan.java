package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
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
                    .setDescription("Couldn't ban " + banned.get().getName() + ". Please check logs or try again.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(
            blob.getStandardEmbed(getNameReadable())
                .setDescription(banned.get().getName() + " was bot banned.")
                .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Ban a user from ever using the bot again.",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate(2023, 2, 28, 12, 47),
            Metadata.parseDate(2024, 8, 20, 12, 3)
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
        return List.of(StuffNThings.getConfig().getCore().getSupportGuild().getId());
    }
}
