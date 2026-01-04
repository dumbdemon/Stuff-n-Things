package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.FreeDictionaryHandler;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Dictionary extends SlashCommandInteraction {

    public Dictionary() {
        super("dictionary", "Look up a word in the dictionary.",
            Mastermind.DEVELOPER,
            CommandCategory.FUN,
            parseDate(2022, 10, 27, 12, 46),
            now()
        );
        addOptions(
            new OptionData(OptionType.STRING, "word", "What to look up", true)
        );
        setDisabledReason("Being reworked with a new API. Stay tuned!");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        //event.deferReply().queue();
        FreeDictionaryHandler ignore = new FreeDictionaryHandler();
    }
}
