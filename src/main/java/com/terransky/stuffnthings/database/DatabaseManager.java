package com.terransky.stuffnthings.database;

import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.DBProperty;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public interface DatabaseManager {

    DatabaseManager INSTANCE = new SQLiteDataSource();

    @NotNull
    static MessageEmbed anErrorOccurred(@NotNull Exception e, @NotNull Logger log) {
        log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
        LogList.error(Arrays.asList(e.getStackTrace()), log);
        EmbedBuilder eb = new EmbedBuilder().setColor(EmbedColors.getError());
        eb.setTitle("Uh-oh")
            .setDescription("An error occurred while executing the command!\n Try again in a moment!");
        return eb.build();
    }

    /**
     * Get a {@link DBProperty} from the database as an Integer.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link DBProperty}
     * @return An integer associated with the property
     */
    Optional<Integer> getFromDBInt(@NotNull EventBlob blob, @NotNull DBProperty property);

    /**
     * Get a {@link DBProperty} from the database as a string.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link DBProperty}
     * @return A string associated with the property
     */
    Optional<String> getFromDBString(@NotNull EventBlob blob, @NotNull DBProperty property);

    /**
     * Get a {@link DBProperty} from the database as a boolean.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link DBProperty}
     * @return A boolean associated with the property
     */
    boolean getFromDBBoolean(@NotNull EventBlob blob, @NotNull DBProperty property);

    void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, int newValue);

    void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, String newValue);

    void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, boolean newValue);

    /**
     * Add the guild to the database and create a user table.
     *
     * @param guild A {@link Guild} object.
     */
    void addGuild(@NotNull Guild guild);

    /**
     * Remove a guild and user table from the database.
     *
     * @param guild A {@link Guild} object.
     */
    void removeGuild(@NotNull Guild guild);

    /**
     * Add a user to a guild's user table
     *
     * @param blob An {@link EventBlob}
     */
    void addUser(@NotNull EventBlob blob);

    /**
     * Remove a user from a guild's user table
     *
     * @param userID The id of the user to remove
     * @param guild  The guild for the target user table
     */
    void removeUser(String userID, Guild guild);
}
