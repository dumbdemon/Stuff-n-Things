package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.database.MongoDBDataSource;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface DatabaseManager {

    /**
     * The main instance of the database.
     */
    DatabaseManager INSTANCE = new MongoDBDataSource();

    /**
     * Get a {@link Property} from the database as an Integer.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link Property}
     * @return An {@link Optional} object associated with the property
     */
    Optional<Object> getFromDatabase(@NotNull EventBlob blob, @NotNull Property property);

    /**
     * Update a property in the database
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link Property} to update.
     * @param newValue The new value of the property.
     */
    <T> void updateProperty(@NotNull EventBlob blob, @NotNull Property property, T newValue);

    /**
     * Add a kill string to the database
     *
     * @param killStorage An enum containing the {@link Property} data.
     * @param killString  The kill string te add
     * @param idReference THe specific database object to store in
     * @return True if up was successful
     */
    boolean addKillString(KillStorage killStorage, String killString, String idReference);

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
     * @param userId The id of the user to remove
     * @param guild  The guild for the target user table
     */
    void removeUser(String userId, Guild guild);

    /**
     * Get count of users serviced
     *
     * @return A long containing the user count
     */
    long getUserCount();

    /**
     * Get count of all guilds the not is in
     *
     * @return A long containing the guild count
     */
    long getGuildsCount();
}