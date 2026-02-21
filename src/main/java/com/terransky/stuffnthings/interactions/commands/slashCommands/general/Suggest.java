package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.SuggestCommand;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Suggest extends SlashCommandInteraction {

    public Suggest() {
        super("suggest-command", "Have something you want the bot to do? Suggest here!",
            Mastermind.DEVELOPER, CommandCategory.GENERAL,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2026, 2, 21, 5, 0)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        event.replyModal(new SuggestCommand().getContructedModal()).queue();
    }
}
