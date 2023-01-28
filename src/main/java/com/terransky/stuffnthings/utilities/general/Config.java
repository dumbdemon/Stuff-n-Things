package com.terransky.stuffnthings.utilities.general;

import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.Dictionary;
import com.terransky.stuffnthings.utilities.command.Formatter;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Config {

    private static final Dotenv config = Dotenv.configure().load();
    private static final String DEFAULT_VALUE = "";
    private static final String DEFAULT_USERNAME = "username";
    private static final String DEFAULT_PASSWORD = "?1213456789";
    private static final String DEFAULT_USER_ID = "000000000000000000";
    private static final String DEFAULT_GUILD_ID = "0000000000000000000";

    private Config() {
    }

    //Main Stuff
    public static boolean isTestingMode() {
        return config.get("TESTING_MODE", "false").equals("true");
    }

    public static boolean isDatabaseEnabled() {
        return config.get("ENABLE_DATABASE", "false").equals("true");
    }

    public static String getDeveloperId() {
        return config.get("OWNER_ID", DEFAULT_USER_ID);
    }

    public static String getSupportGuildId() {
        return config.get("SUPPORT_GUILD_ID", DEFAULT_GUILD_ID);
    }

    public static long getSupportGuildIdLong() {
        return Long.parseLong(getSupportGuildId());
    }

    public static String getSupportGuildInvite() {
        return config.get("SUPPORT_GUILD_INVITE", DEFAULT_VALUE);
    }

    public static String getErrorReportingURL() {
        return config.get("BOT_ERROR_REPORT", DEFAULT_VALUE);
    }

    public static String getBotLogoURL() {
        return config.get("BOT_LOGO", DEFAULT_VALUE);
    }

    public static String getBotUserAgent() {
        return config.get("BOT_USER_AGENT", DEFAULT_VALUE);
    }

    public static String getRepositoryURL() {
        return config.get("REPO_LINK", DEFAULT_VALUE);
    }

    public static String getRequestWebhookURL() {
        return config.get("REQUEST_WEBHOOK", DEFAULT_VALUE);
    }

    public static String getRequestChannelID() {
        return config.get("REQUEST_CHANNEL_ID", DEFAULT_GUILD_ID);
    }

    public static String getTinyURlDomain() {
        return config.get("TINY_URL_DOMAIN", DEFAULT_VALUE);
    }

    //MongoDB
    public static String getApplicationName() {
        return config.get("APPLICATION_NAME", DEFAULT_VALUE);
    }

    public static String getDatabaseName() {
        return config.get("DB_NAME", DEFAULT_VALUE);
    }

    public static String getMongoHostname() {
        return config.get("DB_HOSTNAME", DEFAULT_VALUE);
    }

    /**
     * All usernames, passwords, and tokens are stored here.<br />
     * <b>NOTE:</b> If it is just a token, call {@link #getPassword()} to retrieve it.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public enum Credentials {

        /**
         * The bot's token to login into Discord. <b>The bot cannot run without this set.</b><br />
         * <a href="https://discord.com/developers/applications">Click here to obtain one.</a>
         */
        DISCORD(config.get("TOKEN", DEFAULT_PASSWORD)),
        /**
         * Credentials for the database.
         */
        DATABASE(config.get("DB_USERNAME", DEFAULT_USERNAME), config.get("DB_PASSWORD", DEFAULT_PASSWORD)),
        /**
         * Credentials for the Oxford Dictionary API.<br />
         * If this is not set, the {@link Dictionary} command won't be upserted.
         */
        OXFORD(config.get("OXFORD_ID", DEFAULT_USERNAME), config.get("OXFORD_KEY", DEFAULT_PASSWORD)),
        /**
         * Token for the TinyURL API.
         */
        TINYURL(config.get("TINY_URL_TOKEN", DEFAULT_PASSWORD)),
        /**
         * The token for the Kitsu.io API. Only required to show NSFW titles.
         */
        KITSU_IO(config.get("KITSU_USERNAME", DEFAULT_USERNAME), Formatter.percentEncode(config.get("KITSU_PASSWORD", DEFAULT_PASSWORD))),
        ;

        private final String username;
        private final String password;
        private final Predicate<String> checkUsername = s -> s.equals(DEFAULT_USERNAME) || s.equals(DEFAULT_VALUE);
        private final Predicate<String> checkPassword = s -> s.equals(DEFAULT_PASSWORD) || s.equals(DEFAULT_VALUE);

        /**
         * Contructor for token authorizations.
         *
         * @param token A token from the config.
         */
        Credentials(String token) {
            this(null, token);
        }

        /**
         * Constructor for username and password combo authorizations.
         *
         * @param username A username from the config.
         * @param password A password from the config.
         */
        Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Checks if the credentials have been set.
         *
         * @return True if credentials weren't set.
         */
        public boolean isDefault() {
            if (username == null)
                return checkPassword.test(password);
            return checkUsername.test(username) || checkPassword.test(password);
        }

        /**
         * Get the username.
         *
         * @return A username or null if the credentials is a token.
         */
        @Nullable
        public String getUsername() {
            return username;
        }

        /**
         * Get the password or token.
         *
         * @return A password or token.
         */
        public String getPassword() {
            return password;
        }
    }
}
