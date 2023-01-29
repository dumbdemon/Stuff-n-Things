package com.terransky.stuffnthings.database;

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
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.entry.GuildEntry;
import com.terransky.stuffnthings.database.helpers.entry.KillLock;
import com.terransky.stuffnthings.database.helpers.entry.KillStrings;
import com.terransky.stuffnthings.database.helpers.entry.UserEntry;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final ConnectionString connectionString;
    private final CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    public MongoDBDataSource() {
        this.log = LoggerFactory.getLogger(MongoDBDataSource.class);
        Config.Credentials credentials = Config.Credentials.DATABASE;
        assert credentials.getUsername() != null;
        String username = URLEncoder.encode(credentials.getUsername(), StandardCharsets.UTF_8);
        String password = URLEncoder.encode(credentials.getPassword(), StandardCharsets.UTF_8);
        this.connectionString = new ConnectionString("mongodb+srv://" + username + ":" + password + "@" + Config.getMongoHostname());
    }

    private void ifAnErrorOccurs(String success, String failed, RuntimeException throwable) {
        if (throwable == null) {
            log.info(success);
        } else log.error(failed, throwable);
    }

    @NotNull
    private MongoCollection<GuildEntry> getGuilds(@NotNull MongoClient client) {
        MongoDatabase database = client.getDatabase(Config.getDatabaseName());
        return database.getCollection("guilds", GuildEntry.class).withCodecRegistry(codecRegistry);
    }

    @NotNull
    private MongoCollection<UserEntry> getUsers(@NotNull MongoClient client) {
        MongoDatabase database = client.getDatabase(Config.getDatabaseName());
        return database.getCollection("users", UserEntry.class).withCodecRegistry(codecRegistry);
    }

    @NotNull
    private MongoCollection<KillStrings> getKills(@NotNull MongoClient client) {
        MongoDatabase database = client.getDatabase(Config.getDatabaseName());
        return database.getCollection("sources", KillStrings.class).withCodecRegistry(codecRegistry);
    }

    @NotNull
    private MongoClient getConstructedClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applicationName(Config.getApplicationName() + (Config.isTestingMode() ? "_TEST" : ""))
            .serverApi(ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build()
            )
            .applyConnectionString(connectionString)
            .retryWrites(true)
            .build();

        return MongoClients.create(settings);
    }

    @Override
    public boolean addKillString(@NotNull KillStorage killStorage, String idReference, String killString) {
        Property property = killStorage.getProperty();
        Bson filter = Filters.eq(ID_REFERENCE.getPropertyName(Table.KILL), idReference);

        try (MongoClient client = getConstructedClient()) {
            var strings = getKills(client);
            var finder = new ObjectSubscriber<KillStrings>();
            var updater = new ObjectSubscriber<UpdateResult>();
            strings.find(filter).subscribe(finder);

            if (finder.await().getError() != null) throw finder.getError();

            if (finder.first() == null) {
                var insert = new ObjectSubscriber<InsertOneResult>();

                strings.insertOne(new KillStrings(idReference))
                    .subscribe(insert);

                if (insert.await().getError() != null) throw insert.getError();

                finder = new ObjectSubscriber<>();
                strings.find(filter).subscribe(finder);
                if (finder.await().getError() != null) throw finder.getError();
            }

            KillStrings killStrings = finder.first();
            List<String> killStringsList;
            if (property == KILL_RANDOM) {
                killStringsList = killStrings.getKillRandoms();
            } else {
                killStringsList = killStrings.getKillTargets();
            }
            killStringsList.add(killString);

            strings.updateOne(Filters.and(filter), Updates.set(property.getPropertyName(), killStringsList))
                .subscribe(updater);

            return updater.await().getError() == null;
        }
    }

    @Override
    public Optional<Object> getFromDatabase(@NotNull EventBlob blob, @NotNull Property property) {
        if (!Config.isDatabaseEnabled()) return Optional.empty();
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<?> collection = getCollection(property, client);
            String target = property.getTable().getTarget(blob);

            var subscriber = new ObjectSubscriber<>();
            collection.find(Filters.eq(ID_REFERENCE.getPropertyName(property.getTable()), target)).subscribe(subscriber);

            if (subscriber.await().getError() != null)
                return Optional.empty();

            switch (property.getTable()) {
                case GUILD -> {
                    GuildEntry guildEntry = (GuildEntry) subscriber.first();
                    return guildEntry.getProperty(property);
                }
                case USER -> {
                    UserEntry userEntry = (UserEntry) subscriber.first();
                    return userEntry.getProperty(property, blob.getGuildId());
                }
                case KILL -> {
                    KillStrings killStrings = (KillStrings) subscriber.first();
                    return killStrings.getProperty(property);
                }
                default ->
                    throw new IllegalArgumentException(String.format("Cannot retrieve property of %s from database", property));
            }
        }
    }

    @NotNull
    private MongoCollection<?> getCollection(@NotNull Property property, MongoClient client) {
        MongoCollection<?> collection;
        switch (property.getTable()) {
            case USER -> collection = getUsers(client);
            case GUILD -> collection = getGuilds(client);
            case KILL -> collection = getKills(client);
            default ->
                throw new IllegalArgumentException(String.format("The property %s is used for identification purposes only", property));
        }
        return collection;
    }

    @Override
    public <T> void updateProperty(@NotNull EventBlob blob, @NotNull Property property, T newValue) {
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<?> collection = getCollection(property, client);
            String target = property.getTable().getTarget(blob);
            Bson search = Filters.eq(ID_REFERENCE.getPropertyName(property.getTable()), target);

            var updater = new ObjectSubscriber<UpdateResult>();

            switch (property) {
                case KILL_TIMEOUT -> {
                    var subscriber = getSubscriber(collection, search);

                    UserEntry user = (UserEntry) subscriber.first();
                    List<KillLock> killLocks = new ArrayList<>(user.getKillLocks());
                    for (KillLock lock : killLocks) {
                        if (lock.getGuildReference().equals(blob.getGuildId())) {
                            killLocks.remove(lock);
                            lock.setKillUnderTo(!lock.isKillUnderTo());
                            killLocks.add(lock);
                            collection.updateOne(search, Updates.set(property.getPropertyName(), killLocks)).subscribe(updater);
                            break;
                        }
                    }
                }
                case KILL_ATTEMPTS -> {
                    var subscriber = getSubscriber(collection, search);

                    UserEntry user = (UserEntry) subscriber.first();
                    List<KillLock> killLocks = new ArrayList<>(user.getKillLocks());
                    for (KillLock lock : killLocks) {
                        if (lock.getGuildReference().equals(blob.getGuildId())) {
                            killLocks.remove(lock);
                            lock.setKillAttempts((Long) newValue);
                            killLocks.add(lock);
                            collection.updateOne(search, Updates.set(property.getPropertyName(), killLocks)).subscribe(updater);
                            break;
                        }
                    }
                }
                default ->
                    collection.updateOne(search, Updates.set(property.getPropertyName(), newValue)).subscribe(updater);
            }

            ifAnErrorOccurs(String.format("Property [%s] was updated successfully", property),
                String.format("Unable to update property [%s]", property),
                updater.await().getError());
        }
    }

    @NotNull
    private ObjectSubscriber<?> getSubscriber(@NotNull MongoCollection<?> collection, Bson bson) {
        var subscriber = new ObjectSubscriber<>();
        collection.find(bson).subscribe(subscriber);

        if (subscriber.await().getError() == null) throw subscriber.getError();
        return subscriber;
    }

    @Override
    public void addGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildName = guild.getName(),
            guildId = guild.getId();
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<GuildEntry> guilds = getGuilds(client);

            var finder = new ObjectSubscriber<GuildEntry>();
            var subscriber = new ObjectSubscriber<InsertOneResult>();

            guilds.find(Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), guildId)).subscribe(finder);

            if (finder.await(2, TimeUnit.MINUTES).getError() != null) {
                log.error("Unable to find guild", finder.getError());
                return;
            }

            if (finder.first() == null) {
                guilds.insertOne(new GuildEntry(guildId))
                    .subscribe(subscriber);

                ifAnErrorOccurs(String.format("%s [%S] was added to the database", guildName, guildId),
                    String.format("Unable to add guild %s [%s] to database", guildName, guildId),
                    subscriber.await().getError());
            }
        }
    }

    @Override
    public void removeGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildId = guild.getId(),
            guildName = guild.getName();
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<GuildEntry> guilds = getGuilds(client);

            var subscriber = new ObjectSubscriber<DeleteResult>();

            guilds.deleteOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.GUILD), guildId))
                .subscribe(subscriber);

            ifAnErrorOccurs(String.format("%s [%s] has been removed from the database", guildName, guildId),
                String.format("Unable to remove guild %s [%s] to database", guildName, guildId),
                subscriber.await(2, TimeUnit.MINUTES).getError());
        }
    }

    @Override
    public void addUser(@NotNull EventBlob blob) {
        if (!Config.isDatabaseEnabled()) return;
        String userId = blob.getMemberId(),
            guildId = blob.getGuildId();
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<UserEntry> users = getUsers(client);

            var finder = new ObjectSubscriber<UserEntry>();

            users.find(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), blob.getMemberId())).subscribe(finder);

            if (finder.await(2, TimeUnit.MINUTES).getError() != null) {
                log.error("Couldn't find user", finder.getError());
                return;
            }

            if (finder.getObjects().isEmpty()) {
                var insert = new ObjectSubscriber<InsertOneResult>();
                UserEntry userEntry = new UserEntry(blob.getMemberId());
                userEntry.setKillLocks(List.of(new KillLock(guildId)));
                users.insertOne(userEntry)
                    .subscribe(insert);

                ifAnErrorOccurs(String.format("User of ID %s was added to the database", userId),
                    String.format("Unable to add user of id %s to database", userId),
                    insert.await(2, TimeUnit.MINUTES).getError());
                return;
            }

            UserEntry user = finder.first();
            List<KillLock> killLocks = new ArrayList<>(user.getKillLocks());

            if (killLocks.stream().anyMatch(lock -> lock.getGuildReference().equals(guildId))) {
                log.info("User [{}] already has KillLock for server ID [{}]", userId, guildId);
                return;
            }

            var updater = new ObjectSubscriber<UpdateResult>();
            killLocks.add(new KillLock(guildId));
            users.updateOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId), Updates.set(KILL_LOCK.getPropertyName(), killLocks))
                .subscribe(updater);

            ifAnErrorOccurs(String.format("User of ID %s was updated", userId),
                String.format("Unable to update User of ID %s", userId),
                updater.await(2, TimeUnit.MINUTES).getError());
        }
    }

    @Override
    public void removeUser(String userId, @NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<UserEntry> guilds = getUsers(client);

            var subscriber = new ObjectSubscriber<DeleteResult>();

            guilds.deleteOne(Filters.eq(ID_REFERENCE.getPropertyName(Table.USER), userId))
                .subscribe(subscriber);

            ifAnErrorOccurs(String.format("User of ID %s was removed from the database", userId),
                String.format("Unable to remove user of id %s to database", userId),
                subscriber.await(2, TimeUnit.MINUTES).getError());
        }
    }

    @Override
    public long getUserCount() {
        if (!Config.isDatabaseEnabled()) return Integer.MAX_VALUE;
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<UserEntry> users = getUsers(client);
            var subscriber = new OperationSubscriber<Long>();
            users.countDocuments().subscribe(subscriber);
            return subscriber.await().first();
        }
    }

    @Override
    public long getGuildsCount() {
        if (!Config.isDatabaseEnabled()) return Integer.MAX_VALUE;
        try (MongoClient client = getConstructedClient()) {
            MongoCollection<GuildEntry> guilds = getGuilds(client);
            var subscriber = new OperationSubscriber<Long>();
            guilds.countDocuments().subscribe(subscriber);
            return subscriber.await().first();
        }
    }
}
