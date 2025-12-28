package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class AddToWatchlist extends SlashCommandInteraction {

    public AddToWatchlist() {
        super("add-to-watchlist", "Add something to the watchlist", Mastermind.DEVELOPER, CommandCategory.DEVS,
            parseDate(2023, 1, 27, 22, 35),
            parseDate(2025, 12, 27, 0, 34)
        );
        addOptions(
            new OptionData(OptionType.STRING, "watch-this", "What to watch...", true)
                .setRequiredLength(1, 128)
        );
        setWorking(StuffNThings.getConfig().getCore().getEnableDatabase());
        setDeveloperOnly();
        addRestrictedServer(StuffNThings.getConfig().getCore().getSupportGuild().getId());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        Optional<String> eventOption = Optional.ofNullable(event.getOption("watch-this", OptionMapping::getAsString));
        String watchThis = eventOption.orElseThrow(DiscordAPIException::new);
        boolean success = DatabaseManager.INSTANCE.addKillString(KillStorage.RANDOM, "1", watchThis);

        event.reply(success ? "Success." : "Fail.").useComponentsV2(false).setEphemeral(true).queue();
    }
}
