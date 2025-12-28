package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

public abstract class GameSlashCommandInteraction extends SlashCommandInteraction {

    protected GameSlashCommandInteraction(String name, String description, Mastermind mastermind, CommandCategory category, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(name, description, mastermind, category, createdAt, updatedAt);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given");

        switch (subcommand) {
            case "new" -> newGame(event, blob);
            case "join" -> joinGame(event, blob);
            case "start" -> startGame(event, blob);
            case "last" -> lastGame(event, blob);
            case "cancel" -> cancelGame(event, blob);
        }
    }

    /**
     * Function to create a new game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     */
    protected abstract void newGame(SlashCommandInteractionEvent event, EventBlob blob);

    /**
     * Function to join a game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     */
    protected abstract void joinGame(SlashCommandInteractionEvent event, EventBlob blob);

    /**
     * Function to start game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     */
    protected abstract void startGame(SlashCommandInteractionEvent event, EventBlob blob);

    /**
     * Function to view the stats of a user's last game in a channel.
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     */
    protected abstract void lastGame(SlashCommandInteractionEvent event, EventBlob blob);

    /**
     * Function to cancel a game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     */
    protected abstract void cancelGame(SlashCommandInteractionEvent event, EventBlob blob);

    protected enum GameAction {
        NEW,
        JOIN,
        START,
        LAST,
        CANCEL
        ;

        @NotNull
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
