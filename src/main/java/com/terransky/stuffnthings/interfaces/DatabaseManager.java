package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.dataSources.kitsu.KitsuAuth;
import com.terransky.stuffnthings.database.MongoDBDataSource;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.database.helpers.entry.UserGuildEntry;
import com.terransky.stuffnthings.games.Game;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface DatabaseManager {

    /**
     * The main instance of the database.
     */
    DatabaseManager INSTANCE = new MongoDBDataSource();

    /**
     * Get a {@link Property} from the database.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link Property}
     * @return An {@link Optional} object associated with the property
     * @throws IllegalArgumentException If the provided {@link Property} is of {@link com.terransky.stuffnthings.database.helpers.Property.Table#ROOT Table.ROOT}
     */
    Optional<Object> getFromDatabase(@NotNull EventBlob blob, @NotNull Property property);

    /**
     * Get a {@link Property} from the database.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link Property}
     * @param mapper   The mapping function to apply to a value, if present
     * @return An {@link Optional} object associated with the property
     * @throws IllegalArgumentException If the provided {@link Property} is of {@link com.terransky.stuffnthings.database.helpers.Property.Table#ROOT Table.ROOT}
     */
    default <T> Optional<T> getFromDatabase(@NotNull EventBlob blob, @NotNull Property property, Function<Object, ? extends T> mapper) {
        return getFromDatabase(blob, property)
            .map(mapper);
    }

    /**
     * Get a {@link Property} from the database.
     *
     * @param blob     An {@link EventBlob}
     * @param property A {@link Property}
     * @param fallback What to return if no value was retrieved from the database
     * @param mapper   The mapping function to apply to a value, if present
     * @return The property from the database
     * @throws IllegalArgumentException If the provided {@link Property} is of {@link com.terransky.stuffnthings.database.helpers.Property.Table#ROOT Table.ROOT}
     */
    default <T> T getFromDatabase(@NotNull EventBlob blob, @NotNull Property property, T fallback, Function<Object, T> mapper) {
        return getFromDatabase(blob, property, mapper)
            .orElse(fallback);
    }

    /**
     * Get all properties form the database connected to a specific user.
     *
     * @param userId  A user id
     * @param guildId A guild id
     * @return A {@link UserGuildEntry}
     */
    UserGuildEntry getUserGuildEntry(@NotNull String userId, @NotNull String guildId);

    /**
     * Reset all user properties for the {@link com.terransky.stuffnthings.interactions.commands.slashCommands.fun.Kill /kill} command
     *
     * @param userId  The user id to reset
     * @param guildId The guild id reference for the {@link com.terransky.stuffnthings.database.helpers.entry.PerServer PerServer}
     * @return True if the operation completed without errors.
     */
    boolean resetUserKillProperties(@NotNull String userId, @NotNull String guildId);

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
     * @param idReference THe specific database object to store in
     * @param killString  The kill string te add
     * @return True if up was successful
     */
    boolean addKillString(KillStorage killStorage, String idReference, String killString);

    /**
     * Get the watchlist from the database
     *
     * @return A watchlist
     */
    default List<String> getWatchList() {
        return getFromDatabase(new EventBlob(null, null), Property.KILL_RANDOM, List.of("you"), PropertyMapping::getAsListOfString);
    }

    /**
     * Upload a new {@link KitsuAuth} to the database, or save it to the file system if the database is not enabled
     *
     * @param kitsuAuth A {@link KitsuAuth} generated with
     *                  {@link com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler#upsertAuthorizationToken() KitsuHandler.upsertAuthorizationToken()}
     * @return True if the operation was successful
     */
    boolean uploadKitsuAuth(KitsuAuth kitsuAuth);

    /**
     * Upload game data to the database
     *
     * @param <T>   A {@link Game}
     * @param blob  An {@link EventBlob}
     * @param games The type of game
     * @param game  The game data to upload
     */
    <T extends Game<?>> void uploadGameData(@NotNull EventBlob blob, Property.Games games, T game);

    /**
     * Get the last game data that happened on a channel
     *
     * @param blob      An {@link EventBlob}
     * @param channelId The channel where the game occurred
     * @param games     The type of game
     * @return An {@link Optional} containing generic {@link Game} data
     */
    Optional<? extends Game<?>> getGameData(@NotNull EventBlob blob, String channelId, @NotNull Property.Games games);

    /**
     * Get the last game data that happened on a channel
     *
     * @param blob      An {@link EventBlob}
     * @param channelId The channel where the game occurred
     * @param games     The type of game
     * @param mapper    The mapping function to apply to a value, if present
     * @param <T>       A {@link Game}
     * @return An {@link Optional} containing game data
     */
    default <T> Optional<T> getGameData(@NotNull EventBlob blob, String channelId, @NotNull Property.Games games, Function<? super Game<?>, T> mapper) {
        return getGameData(blob, channelId, games)
            .map(mapper);
    }

    /**
     * Get the stored {@link KitsuAuth} from the database, if enabled; otherwise, get it from filesystem.
     *
     * @return An {@link Optional} of a {@link KitsuAuth}
     */
    Optional<KitsuAuth> getKitsuAuth();

    /**
     * Add the guild to the database
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
     * Get count of users serviced.
     *
     * @return A long containing the user count
     */
    long getUserCount();

    /**
     * Get count of users serviced. If the database is not enabled, it will get the count of users stored in the cache.
     * <p>
     * It is recommended to set cache flags to get an accurate count.
     *
     * @param jda The JDA instance.
     * @return A long containing the user count
     */
    default long getUserCount(@NotNull JDA jda) {
        if (Config.isDatabaseEnabled())
            return getUserCount();
        return jda.getUserCache().stream().count();
    }

    /**
     * Get count of all guilds the bot is in.
     *
     * @return A long containing the guild count
     */
    long getGuildsCount();

    /**
     * Get count of all guilds the bot is in. If the database is not enabled, it will get the count of guilds stored in the cache.
     * <p>
     * It is recommended to set cache flags to get an accurate count.
     *
     * @param jda The JDA instance.
     * @return A long containing the guild count
     */
    default long getGuildsCount(@NotNull JDA jda) {
        if (Config.isDatabaseEnabled())
            return getGuildsCount();
        return jda.getGuildCache().stream().count();
    }
}
