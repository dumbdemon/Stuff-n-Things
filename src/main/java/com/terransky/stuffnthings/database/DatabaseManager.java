package com.terransky.stuffnthings.database;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface DatabaseManager {

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

    boolean addKillString(Property property, String string, String idReference);

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

    long getUserCount();

    long getGuildsCount();
}
