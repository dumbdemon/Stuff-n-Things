package com.terransky.StuffnThings.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

//TODO: Set up MongoDB
public class SQLiteDataSource {
    private static final Logger log = LoggerFactory.getLogger(SQLiteDataSource.class);
    private static final Dotenv bConfig = Dotenv.configure().load();
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;
    private static final HikariPoolMXBean poolBean;

    static {
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
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:database.sqlite");
        config.setConnectionTestQuery("SELECT 1");
        config.setUsername(bConfig.get("DB_USERNAME"));
        config.setPassword(bConfig.get("DB_PASSWORD"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");
        ds = new HikariDataSource(config);
        poolBean = ds.getHikariPoolMXBean();

        try (final Statement stmt = getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS guilds (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "guild_id VARCHAR(20) UNIQUE NOT NULL," +
                "killcmd_max INTEGER NOT NULL DEFAULT 5," +
                "killcmd_timeout INTEGER NOT NULL DEFAULT 300000," +
                "reporting_wh TEXT," +
                "reporting_ar TEXT" +
                ");");
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    private SQLiteDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void restartConnection() {
        poolBean.softEvictConnections();
    }
}
