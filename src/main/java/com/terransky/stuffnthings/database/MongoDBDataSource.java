package com.terransky.stuffnthings.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuAuth;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.entry.*;
import com.terransky.stuffnthings.games.Bingo.BingoGame;
import com.terransky.stuffnthings.games.Game;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Formatter;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.mongodb.reactivestreams.client.MongoClients.getDefaultCodecRegistry;
import static com.terransky.stuffnthings.database.helpers.Property.*;
import static com.terransky.stuffnthings.database.helpers.Subscribers.ObjectSubscriber;
import static com.terransky.stuffnthings.database.helpers.Subscribers.OperationSubscriber;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * <a href="https://www.mongodb.com/docs/drivers/java/sync/current/quick-start/">MongoDB Documentation</a>
 */
public class MongoDBDataSource implements DatabaseManager {

    private final Logger log;
    private final CodecRegistry CODEC_REGISTRY;
    private final MongoDatabase DATABASE;
    private final String KITSU_ID_REFERENCE = "0";
    private final String THAT_NAME = Formatter.getNameOfClass(PerServer.class);

    public MongoDBDataSource() {
        this.log = LoggerFactory.getLogger(MongoDBDataSource.class);
        Config.Credentials credentials = Config.Credentials.DATABASE;
        assert credentials.getUsername() != null;
        String username = URLEncoder.encode(credentials.getUsername(), StandardCharsets.UTF_8);
        String password = URLEncoder.encode(credentials.getPassword(), StandardCharsets.UTF_8);

        this.CODEC_REGISTRY = fromRegistries(getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClient client = MongoClients.create(
            MongoClientSettings.builder()
                .applicationName(Config.getApplicationName() + (Config.isTestingMode() ? "_TEST" : ""))
                .serverApi(ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build()
                )
                .applyConnectionString(new ConnectionString("mongodb+srv://" + username + ":" + password + "@" + Config.getMongoHostname()))
                .retryWrites(true)
                .build()
        );
        this.DATABASE = client.getDatabase(Config.getDatabaseName());
    }

    private void ifAnErrorOccurs(String success, String failed, RuntimeException throwable) {
        if (throwable == null) {
            log.info(success);
        } else log.error(failed, throwable);
    }

    @NotNull
    private MongoCollection<GuildEntry> getGuilds() {
        return DATABASE.getCollection("guilds", GuildEntry.class).withCodecRegistry(CODEC_REGISTRY);
    }

    @NotNull
    private MongoCollection<UserEntry> getUsers() {
        return DATABASE.getCollection("users", UserEntry.class).withCodecRegistry(CODEC_REGISTRY);
    }

    @NotNull
    private MongoCollection<KillStrings> getKills() {
        return DATABASE.getCollection("sources", KillStrings.class).withCodecRegistry(CODEC_REGISTRY);
    }

    @Override
    public boolean addKillString(@NotNull KillStorage killStorage, String idReference, String killString) {
        Property property = killStorage.getProperty();
        Bson filter = Filters.eq(ID_REFERENCE.getPropertyName(Table.KILL), idReference);

        var strings = getKills();
        var finder = getSubscriber(strings, filter);

        if (finder.await().hasError()) throw finder.getError();

        if (finder.first().isEmpty()) {
            var insert = new ObjectSubscriber<InsertOneResult>();

            strings.insertOne(new KillStrings(idReference))
                .subscribe(insert);

            if (insert.await().hasError()) throw insert.getError();

            finder = getSubscriber(strings, filter);
            if (finder.await().hasError()) throw finder.getError();
        }

        var updater = new ObjectSubscriber<UpdateResult>();
        KillStrings killStrings = finder.first()
            .orElse(new KillStrings(idReference));
        List<String> killStringsList;
        if (property == KILL_RANDOM) {
            killStringsList = killStrings.getKillRandoms();
        } else {
            killStringsList = killStrings.getKillTargets();
        }
        killStringsList.add(killString);

        strings.updateOne(filter, Updates.set(property.getPropertyName(), killStringsList))
            .subscribe(updater);

        return updater.await().hasNoError();
    }

    @Override
    public boolean uploadKitsuAuth(KitsuAuth kitsuAuth) {
        if (!Config.isDatabaseEnabled()) {
            try {
                kitsuAuth.saveAsJsonFile(new File(KitsuHandler.FILE_NAME));
                return true;
            } catch (IOException e) {
                log.error("Error saving KitsuAuth", e);
                return false;
            }
        }

        if (kitsuAuth.getIdReference() == null)
            kitsuAuth.setIdReference(KITSU_ID_REFERENCE);
        MongoCollection<KitsuAuth> auths = DATABASE.getCollection("kitsuauth", KitsuAuth.class).withCodecRegistry(CODEC_REGISTRY);

        var finder = getSubscriber(auths, Filters.eq("idReference", KITSU_ID_REFERENCE));

        if (finder.await().hasError()) throw finder.getError();

        if (finder.first().isEmpty()) {
            var inserter = new ObjectSubscriber<InsertOneResult>();
            auths.insertOne(kitsuAuth).subscribe(inserter);
            return inserter.await().hasNoError();
        }

        var replacer = new ObjectSubscriber<UpdateResult>();
        auths.replaceOne(Filters.eq("idReference", KITSU_ID_REFERENCE), kitsuAuth)
            .subscribe(replacer);
        return replacer.await().hasNoError();
    }

    @Override
    public Optional<KitsuAuth> getKitsuAuth() {
        if (!Config.isDatabaseEnabled()) {
            File kitsuAuth = new File(KitsuHandler.FILE_NAME);
            if (kitsuAuth.exists())
                try {
                    return Optional.ofNullable(new ObjectMapper().readValue(kitsuAuth, KitsuAuth.class));
                } catch (IOException e) {
                    log.error("Unable to get KitsuAuth", e);
                    return Optional.empty();
                }
            return Optional.empty();
        }

        MongoCollection<KitsuAuth> auths = DATABASE.getCollection("kitsuauth", KitsuAuth.class).withCodecRegistry(CODEC_REGISTRY);

        var finder = getSubscriber(auths, Filters.eq("idReference", KITSU_ID_REFERENCE));

        finder.await().hasNoError("Failed to get KitsuAuth");

        return finder.first();
    }

    @Override
    public <T extends Game<?>> void uploadGameData(@NotNull EventBlob blob, T game, Games games) {
        if (!Config.isDatabaseEnabled()) return;
        MongoCollection<GuildEntry> guilds = getGuilds();
        if (Games.BINGO.equals(games)) {
            var finder = getSubscriber(guilds, Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), blob.getGuildId()));

            GuildEntry guildEntry = finder.first().orElse(new GuildEntry(blob.getGuildId()));

            List<BingoGame> bingoGames = guildEntry.getBingoGames() == null ? new ArrayList<>() : guildEntry.getBingoGames();
            bingoGames.stream().filter(bingoGame -> bingoGame.getChannelId().equals(game.getChannelId()))
                .findFirst()
                .ifPresent(bingoGames::remove);
            bingoGames.add((BingoGame) game);

            var updater = new ObjectSubscriber<UpdateResult>();

            guilds.updateOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), blob.getGuildId()), Updates.set(LAST_BINGO.getPropertyName(), bingoGames))
                .subscribe(updater);
            updater.await().hasNoError("Unable to upload Game data");
        }
    }

    @Override
    public Optional<? extends Game<?>> getLastGameData(@NotNull EventBlob blob, String channelId, @NotNull Games games) {
        if (!Config.isDatabaseEnabled()) return Optional.empty();
        MongoCollection<GuildEntry> users = getGuilds();
        var finder = getSubscriber(users, Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), blob.getMemberId()));

        GuildEntry guildEntry = finder.first().orElse(new GuildEntry(blob.getGuildId()));

        if (Games.BINGO.equals(games)) {
            return guildEntry.getBingoGames().stream().filter(game -> game.getChannelId().equals(channelId))
                .findFirst();
        }
        throw new IllegalArgumentException("Games cannot be null");
    }

    @Override
    public Optional<Object> getFromDatabase(@NotNull EventBlob blob, @NotNull Property property) {
        if (!Config.isDatabaseEnabled()) return Optional.empty();
        MongoCollection<?> collection = getCollection(property);
        String target = property.getTable().getTarget(blob);

        var subscriber = getSubscriber(collection, Filters.eq(ID_REFERENCE.getPropertyName(property.getTable()), target));

        if (!subscriber.await().hasNoError(String.format("Failed to get property %s from database", property))) {
            return Optional.empty();
        }

        switch (property.getTable()) {
            case GUILD -> {
                GuildEntry guildEntry = subscriber.first().map(GuildEntry::asGuildEntry).orElse(new GuildEntry(blob.getGuildId()));
                return guildEntry.getProperty(property);
            }
            case USER -> {
                UserEntry userEntry = subscriber.first().map(UserEntry::asUserEntry).orElse(new UserEntry(blob.getGuildId()));
                return userEntry.getProperty(property, blob.getGuildId());
            }
            case KILL -> {
                KillStrings killStrings = subscriber.first().map(entry -> (KillStrings) entry)
                    .orElse(new KillStrings(property.getTable().getTarget(blob)));
                return killStrings.getProperty(property);
            }
            default ->
                throw new IllegalArgumentException(String.format("Cannot retrieve property of %s from database", property));
        }
    }

    @Override
    public UserGuildEntry getUserGuildEntry(@NotNull String userId, @NotNull String guildId) {
        var guilds = getGuilds();
        var users = getUsers();
        var guildGetter = new ObjectSubscriber<GuildEntry>();
        var userGetter = new ObjectSubscriber<UserEntry>();

        guilds.find(Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), guildId)).subscribe(guildGetter);
        users.find(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId)).subscribe(userGetter);

        GuildEntry guildEntry = guildGetter.await().first().orElse(new GuildEntry(guildId));

        PerServer perServer = userGetter.await().first()
            .orElse(new UserEntry(userId))
            .getPerServers().stream().filter(lock -> lock.getGuildReference().equals(guildId))
            .findFirst().orElse(new PerServer(guildId));

        return new UserGuildEntry(perServer)
            .setMaxKills(guildEntry.getKillMaximum())
            .setServerTimeout(guildEntry.getKillTimeout());
    }

    @Override
    public boolean resetUserKillProperties(@NotNull String userId, @NotNull String guildId) {
        MongoCollection<UserEntry> users = getUsers();
        var userGetter = new ObjectSubscriber<UserEntry>();
        Bson target = Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId);

        users.find(target).subscribe(userGetter);

        List<PerServer> perServers = new ArrayList<>(userGetter.await().first().orElse(new UserEntry(guildId)).getPerServers());
        perServers.stream().filter(server -> server.getGuildReference().equals(guildId))
            .findFirst()
            .ifPresent(perServers::remove);

        perServers.add(new PerServer(guildId));

        var updater = new ObjectSubscriber<UpdateResult>();

        users.updateOne(target, Updates.set(PER_SERVER.getPropertyName(), perServers))
            .subscribe(updater);

        return updater.await().hasNoError();
    }

    @NotNull
    private MongoCollection<?> getCollection(@NotNull Property property) {
        MongoCollection<?> collection;
        switch (property.getTable()) {
            case USER -> collection = getUsers();
            case GUILD -> collection = getGuilds();
            case KILL -> collection = getKills();
            default ->
                throw new IllegalArgumentException(String.format("The property %s is used for identification purposes only", property));
        }
        return collection;
    }

    @Override
    public <T> void updateProperty(@NotNull EventBlob blob, @NotNull Property property, T newValue) {
        MongoCollection<?> collection = getCollection(property);
        String target = property.getTable().getTarget(blob);
        Bson search = Filters.eq(ID_REFERENCE.getPropertyName(property.getTable()), target);

        var updater = new ObjectSubscriber<UpdateResult>();

        switch (property) {
            case KILL_TIMEOUT -> {
                var subscriber = getSubscriber(collection, search);

                UserEntry user = subscriber.first().map(UserEntry::asUserEntry).orElse(new UserEntry(blob.getGuildId()));
                List<PerServer> perServers = new ArrayList<>(user.getPerServers());
                PerServer perServer = perServers.stream().filter(lock -> lock.getGuildReference().equals(blob.getGuildId()))
                    .findFirst()
                    .orElse(new PerServer(blob.getGuildId()));

                perServers.remove(perServer);
                perServer.setKillUnderTo((Boolean) newValue);
                perServers.add(perServer);
                collection.updateOne(search, Updates.set(PER_SERVER.getPropertyName(), perServers)).subscribe(updater);
            }
            case KILL_ATTEMPTS -> {
                var subscriber = getSubscriber(collection, search);

                UserEntry user = subscriber.first().map(UserEntry::asUserEntry).orElse(new UserEntry(blob.getGuildId()));
                List<PerServer> perServers = new ArrayList<>(user.getPerServers());
                PerServer perServer = perServers.stream().filter(lock -> lock.getGuildReference().equals(blob.getGuildId()))
                    .findFirst()
                    .orElse(new PerServer(blob.getGuildId()));

                perServers.remove(perServer);
                perServer.setKillAttempts((Long) newValue);
                perServers.add(perServer);
                collection.updateOne(search, Updates.set(PER_SERVER.getPropertyName(), perServers)).subscribe(updater);
            }
            case KILL_END_DATE -> {
                var subscriber = getSubscriber(collection, search);

                UserEntry user = subscriber.first().map(UserEntry::asUserEntry).orElse(new UserEntry(blob.getGuildId()));
                List<PerServer> perServers = new ArrayList<>(user.getPerServers());
                PerServer perServer = perServers.stream().filter(lock -> lock.getGuildReference().equals(blob.getGuildId()))
                    .findFirst()
                    .orElse(new PerServer(blob.getGuildId()));

                perServers.remove(perServer);
                if (newValue instanceof OffsetDateTime dateTime) {
                    perServer.setKillEndTime(dateTime.format(DateTimeFormatter.ISO_INSTANT));
                } else {
                    perServer.setKillEndTime((String) newValue);
                }
                perServers.add(perServer);
                collection.updateOne(search, Updates.set(PER_SERVER.getPropertyName(), perServers)).subscribe(updater);
            }
            default ->
                collection.updateOne(search, Updates.set(property.getPropertyName(), newValue)).subscribe(updater);
        }

        ifAnErrorOccurs(String.format("Property [%s] was updated successfully", property),
            String.format("Unable to update property [%s]", property),
            updater.await().getError());
    }

    @NotNull
    private <T> ObjectSubscriber<T> getSubscriber(@NotNull MongoCollection<T> collection, Bson bson) {
        ObjectSubscriber<T> subscriber = new ObjectSubscriber<>();
        collection.find(bson).subscribe(subscriber);

        if (subscriber.await().hasError()) throw subscriber.getError();
        return subscriber;
    }

    @Override
    public void addGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildName = guild.getName(),
            guildId = guild.getId();
        MongoCollection<GuildEntry> guilds = getGuilds();

        var finder = getSubscriber(guilds, Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), guildId));
        var subscriber = new ObjectSubscriber<InsertOneResult>();

        if (finder.await(2, TimeUnit.MINUTES).hasNoError(String.format("An error occurred whilst finding guild with id %s", guildId)))
            return;

        if (finder.first().isEmpty()) {
            guilds.insertOne(new GuildEntry(guildId))
                .subscribe(subscriber);

            ifAnErrorOccurs(String.format("%s [%S] was added to the database", guildName, guildId),
                String.format("Unable to add guild %s [%s] to database", guildName, guildId),
                subscriber.await().getError());
        }
    }

    @Override
    public void removeGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildId = guild.getId(),
            guildName = guild.getName();
        MongoCollection<GuildEntry> guilds = getGuilds();

        var subscriber = new ObjectSubscriber<DeleteResult>();

        guilds.deleteOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), guildId))
            .subscribe(subscriber);

        ifAnErrorOccurs(String.format("%s [%s] has been removed from the database", guildName, guildId),
            String.format("Unable to remove guild %s [%s] to database", guildName, guildId),
            subscriber.await(2, TimeUnit.MINUTES).getError());
    }

    @Override
    public void addUser(@NotNull EventBlob blob) {
        if (!Config.isDatabaseEnabled()) return;
        String userId = blob.getMemberId(),
            guildId = blob.getGuildId();
        MongoCollection<UserEntry> users = getUsers();

        var finder = getSubscriber(users, Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), blob.getMemberId()));

        if (finder.await(2, TimeUnit.MINUTES).getError() != null) {
            log.error("Couldn't find user", finder.getError());
            return;
        }

        if (finder.getObjects().isEmpty()) {
            var insert = new ObjectSubscriber<InsertOneResult>();
            UserEntry userEntry = new UserEntry(blob.getMemberId());
            userEntry.setPerServers(List.of(new PerServer(guildId)));
            users.insertOne(userEntry)
                .subscribe(insert);

            ifAnErrorOccurs(String.format("User of ID %s was added to the database", userId),
                String.format("Unable to add user of id %s to database", userId),
                insert.await(2, TimeUnit.MINUTES).getError());
            return;
        }

        UserEntry user = finder.first()
            .orElseThrow(finder::getError);
        List<PerServer> perServers = new ArrayList<>(user.getPerServers());

        if (perServers.stream().anyMatch(lock -> lock.getGuildReference().equals(guildId))) {
            log.info("User [{}] already has {} for server ID [{}]", userId, THAT_NAME, guildId);
            return;
        }

        var updater = new ObjectSubscriber<UpdateResult>();
        perServers.add(new PerServer(guildId));
        users.updateOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId), Updates.set(PER_SERVER.getPropertyName(), perServers))
            .subscribe(updater);

        ifAnErrorOccurs(String.format("User of ID %s was updated", userId),
            String.format("Unable to update User of ID %s", userId),
            updater.await(2, TimeUnit.MINUTES).getError());
    }

    @Override
    public void removeUser(String userId, @NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        MongoCollection<UserEntry> users = getUsers();

        var finder = getSubscriber(users, Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId));

        List<PerServer> perServers = finder.first().orElse(new UserEntry(guild.getId())).getPerServers();
        PerServer perServer = perServers.stream().filter(lock -> lock.getGuildReference().equals(guild.getId()))
            .findFirst()
            .orElseThrow(finder::getError);

        perServers.remove(perServer);
        var updater = new ObjectSubscriber<UpdateResult>();

        users.updateOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId), Updates.set(PER_SERVER.getPropertyName(), perServers))
            .subscribe(updater);

        ifAnErrorOccurs(String.format("A user's %s was removed from the database", THAT_NAME),
            String.format("Unable to remove a user's %s from the database", THAT_NAME),
            updater.await(2, TimeUnit.MINUTES).getError());
    }

    @Override
    public long getUserCount() {
        MongoCollection<UserEntry> users = getUsers();
        var subscriber = new OperationSubscriber<Long>();
        users.countDocuments().subscribe(subscriber);
        return subscriber.await().first().orElseThrow(subscriber::getError);
    }

    @Override
    public long getGuildsCount() {
        MongoCollection<GuildEntry> guilds = getGuilds();
        var subscriber = new OperationSubscriber<Long>();
        guilds.countDocuments().subscribe(subscriber);
        return subscriber.await().first().orElseThrow(subscriber::getError);
    }
}
