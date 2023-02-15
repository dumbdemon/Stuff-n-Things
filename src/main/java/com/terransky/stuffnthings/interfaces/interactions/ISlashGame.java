package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ISlashGame extends ICommandSlash {

    /**
     * Get the {@link Metadata} object of an {@link ICommandSlash}.
     * <p>
     * <b>NOTE:</b> the {@link #execute(SlashCommandInteractionEvent, EventBlob)} has already been constructed, and you should add the subcommands for
     * "new", "join", "start", and "last" with options if any.
     *
     * @return A {@link Metadata} object.
     */
    @Override
    Metadata getMetadata();

    /**
     * The constructed execute function for all games.
     *
     * @param event A Discord {@link SlashCommandInteractionEvent event}
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws FailedInteractionException Any exception thrown that could prevent operation.
     * @throws IOException                Potentially could be thrown during network operations
     * @throws ExecutionException         Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} could not be completed
     * @throws InterruptedException       Thrown when a {@link java.util.concurrent.CompletableFuture CompletableFuture}.
     *                                    {@link java.util.concurrent.CompletableFuture#get() get()} call gets interrupted.
     */
    @Override
    default void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given");
        EmbedBuilder response = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle(getNameReadable());

        switch (subcommand) {
            case "new" -> newGame(event, blob, response);
            case "join" -> joinGame(event, blob, response);
            case "start" -> startGame(event, blob, response);
            case "last" -> lastGame(event, blob, response);
        }
    }

    /**
     * Function to create a new game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     * @param response An {@link EmbedBuilder}
     */
    void newGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response);

    /**
     * Function to join a game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     * @param response An {@link EmbedBuilder}
     */
    void joinGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response);

    /**
     * Function to start game
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     * @param response An {@link EmbedBuilder}
     */
    void startGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response);

    /**
     * Function to view the stats of a user's last game in a channel.
     *
     * @param event    A {@link SlashCommandInteractionEvent}
     * @param blob     An {@link EventBlob}
     * @param response An {@link EmbedBuilder}
     */
    void lastGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response);
}
