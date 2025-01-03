package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.general.configobjects.CoreConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AddToWatchlist implements ICommandSlash {
    private final CoreConfig CORE_CONFIG = StuffNThings.getConfig().getCore();

    @Override
    public String getName() {
        return "add-to-watchlist";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Add something to the watchlist",
            Mastermind.DEVELOPER, CommandCategory.DEVS,
            Metadata.parseDate(2023, 1, 27, 22, 35),
            Metadata.parseDate(2024, 8, 20, 12, 3)
        )
            .addOptions(
                new OptionData(OptionType.STRING, "watch-this", "What to watch...", true)
                    .setRequiredLength(1, 128)
            );
    }

    @Override
    public boolean isWorking() {
        return CORE_CONFIG.getEnableDatabase();
    }

    @Override
    public boolean isDeveloperCommand() {
        return !ICommandSlash.super.isDeveloperCommand();
    }

    @Override
    public List<Long> getServerRestrictions() {
        return List.of(CORE_CONFIG.getSupportGuild().getId());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        Optional<String> eventOption = Optional.ofNullable(event.getOption("watch-this", OptionMapping::getAsString));
        String watchThis = eventOption.orElseThrow(DiscordAPIException::new);
        boolean success = DatabaseManager.INSTANCE.addKillString(KillStorage.RANDOM, "1", watchThis);

        event.reply(success ? "Success." : "Fail.").setEphemeral(true).queue();
    }
}
