package com.terransky.stuffnthings.database;

import com.terransky.stuffnthings.listeners.ListeningForEvents;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.DBProperty;
import com.terransky.stuffnthings.utilities.general.LogList;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Optional;

public class SQLiteDataSource implements DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(SQLiteDataSource.class);
    private HikariDataSource ds;

    public SQLiteDataSource() {
        if (!Config.isDatabaseEnabled()) return;
        try {
            final File dbFile = new File("database.sqlite");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    log.info("Created database file");
                } else {
                    log.error("Could not create database file");
                }
            }
        } catch (IOException e) {
            log.error("{} : {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), SQLiteDataSource.class);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.sqlite");
        config.setConnectionTestQuery("SELECT 1");
        Config.Credentials dbLogin = Config.Credentials.DATABASE;
        if (dbLogin.isDefault())
            throw new IllegalArgumentException("Cannot initialize database without credentials.");
        config.setUsername(dbLogin.getUsername());
        config.setPassword(dbLogin.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");
        ds = new HikariDataSource(config);

        try (final Statement stmt = getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS guilds (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "guild_id VARCHAR(20) UNIQUE NOT NULL," +
                String.format("%s INTEGER NOT NULL DEFAULT 5,", DBProperty.KILLS_MAX.getId()) +
                String.format("%s INTEGER NOT NULL DEFAULT 300000", DBProperty.KILLS_TIMEOUT.getId()) +
                ");");
        } catch (SQLException e) {
            log.error("{} : {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), SQLiteDataSource.class);
        }
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private void runQuery(String query) {
        try (final PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private ResultSet getResultSet(@NotNull EventBlob blob, @NotNull DBProperty property) {
        String query = "SELECT " + property.getId() + " FROM " + (property.getTable().equals(DBProperty.Table.SERVER) ?
            "guilds WHERE guild_id = " + blob.getGuildId() :
            "users_ WHERE user_id = " + blob.getMemberId());

        try (final PreparedStatement statement = getConnection()
            .prepareStatement(query)) {
            return statement.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Integer> getFromDBInt(@NotNull EventBlob blob, @NotNull DBProperty property) {
        try (final ResultSet rs = getResultSet(blob, property)) {
            return Optional.of(rs.getInt(property.getId()));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getFromDBString(@NotNull EventBlob blob, @NotNull DBProperty property) {
        try (final ResultSet rs = getResultSet(blob, property)) {
            return Optional.of(rs.getString(property.getId()));
        } catch (SQLException e) {
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
            return Optional.empty();
        }
    }

    @Override
    public boolean getFromDBBoolean(@NotNull EventBlob blob, @NotNull DBProperty property) {
        try (final ResultSet rs = getResultSet(blob, property)) {
            return rs.getInt(property.getId()) == 1;
        } catch (SQLException e) {
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
            return false;
        }
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, int newValue) {
        String table = property.getTable().equals(DBProperty.Table.SERVER) ? "guilds" : "users_" + blob.getGuildId(),
            target = property.getTable().equals(DBProperty.Table.SERVER) ? "guild_id" : "user_id";
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
            table,
            property.getId(),
            newValue,
            target,
            property.getTable().equals(DBProperty.Table.SERVER) ? blob.getGuildId() : blob.getMemberId()
        );

        runQuery(query);
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, String newValue) {
        String table = property.getTable().equals(DBProperty.Table.SERVER) ? "guilds" : "users_" + blob.getGuildId(),
            target = property.getTable().equals(DBProperty.Table.SERVER) ? "guild_id" : "user_id";
        String query = String.format("UPDATE %s SET %s = \"%s\" WHERE %s = %s",
            table,
            property.getId(),
            newValue,
            target,
            property.getTable().equals(DBProperty.Table.SERVER) ? blob.getGuildId() : blob.getMemberId()
        );

        runQuery(query);
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, boolean newValue) {
        String table = property.getTable().equals(DBProperty.Table.SERVER) ? "guilds" : "users_" + blob.getGuildId(),
            target = property.getTable().equals(DBProperty.Table.SERVER) ? "guild_id" : "user_id";
        String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",
            table,
            property.getId(),
            newValue ? 1 : 0,
            target,
            property.getTable().equals(DBProperty.Table.SERVER) ? blob.getGuildId() : blob.getMemberId()
        );

        runQuery(query);
    }

    @Override
    public void addGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildName = guild.getName(), guildId = guild.getId();
        try (final PreparedStatement sStmt = getConnection()
            .prepareStatement("INSERT OR IGNORE INTO guilds(guild_id) VALUES(?)")) {
            sStmt.setString(1, guildId);
            sStmt.execute();
            log.info("{}[{}] was added to the database", guildName, guildId);

            try (final PreparedStatement stmt = getConnection()
                .prepareStatement(String.format("CREATE TABLE IF NOT EXISTS users_%s (", guildId) +
                    "user_id TEXT PRIMARY KEY," +
                    String.format("%s INTEGER DEFAULT 0,", DBProperty.KILL_ATTEMPTS.getId()) +
                    String.format("%s INTEGER DEFAULT 0", DBProperty.KILL_TIMEOUT.getId()) +
                    ");")) {
                stmt.execute();
                log.info("Users table for {}[{}] is ready", guildName, guildId);
            }
        } catch (SQLException e) {
            log.error("{} : {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
        }
    }

    @Override
    public void removeGuild(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
        String guildId = guild.getId(),
            guildName = guild.getName();
        try {
            try (final PreparedStatement stmt = getConnection()
                .prepareStatement("DELETE FROM guilds WHERE guild_id = ?")) {
                stmt.setString(1, guildId);
                stmt.execute();
                log.info("{} [{}] has been removed from the guilds table", guildName, guildId);
            }

            try (final PreparedStatement stmt = getConnection()
                .prepareStatement("DROP TABLE users_" + guildId)) {
                stmt.execute();
                log.info("Users table for {} [{}] has been removed from the database", guildName, guildId);
            }
        } catch (SQLException e) {
            log.error("{} : {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
        }
    }

    @Override
    public void addUser(@NotNull EventBlob blob) {
        if (!Config.isDatabaseEnabled()) return;
        try (final PreparedStatement stmt = getConnection()
            .prepareStatement(String.format("INSERT OR IGNORE INTO users_%s (user_id) VALUES(?)", blob.getGuildId()))) {
            stmt.setString(1, blob.getMemberId());
            stmt.execute();
        } catch (SQLException e) {
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
        }
    }

    @Override
    public void removeUser(String userID, Guild guild) {
        if (!Config.isDatabaseEnabled()) return;

        try (final PreparedStatement stmt = getConnection()
            .prepareStatement("DELETE FROM ? WHERE user_id = ?")) {
            stmt.setString(1, "users_" + guild.getId());
            stmt.setString(2, userID);
            stmt.execute();
            log.info("User {} left {} [{}]. Users table has been updated", userID, guild.getName(), guild.getId());
        } catch (SQLException e) {
            log.error("{} : {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
        }
    }
}
