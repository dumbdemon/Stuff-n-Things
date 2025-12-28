package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BotBan extends SlashCommandInteraction {

    public BotBan() {
        super("bot-ban", "Ban a user from ever using the bot again.", Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 28, 12, 47),
            parseDate(2025, 12, 27, 0, 41)
        );
        setDeveloperOnly();
        addRestrictedServer(StuffNThings.getConfig().getCore().getSupportGuild().getId());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        Optional<User> banned = Optional.ofNullable(event.getOption("user", OptionMapping::getAsUser));

        if (banned.isEmpty()) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this, "No user given.", BotColors.ERROR)
            ).setEphemeral(true).queue();
            return;
        }

        if (DatabaseManager.INSTANCE.botBan(banned.get(), false)) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this,
                    String.format("Couldn't ban %s. Please check logs or try again.", banned.get().getName()),
                    BotColors.ERROR
                )
            ).setEphemeral(true).queue();
            return;
        }

        event.replyComponents(
            StandardResponse.getResponseContainer(this, String.format("Banned %s.", banned.get().getName()))
        ).setEphemeral(true).queue();
    }
}
