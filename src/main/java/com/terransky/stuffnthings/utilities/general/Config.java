package com.terransky.stuffnthings.utilities.general;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv config = Dotenv.configure().load();

    private Config() {
    }

    //Main Stuff
    public static String getToken() {
        return config.get("TOKEN", "");
    }

    public static boolean isTestingMode() {
        return config.get("TESTING_MODE", "false").equals("true");
    }

    public static boolean isDatabaseEnabled() {
        return config.get("ENABLE_DATABASE", "false").equals("true");
    }

    public static String getOwnerId() {
        return config.get("OWNER_ID", "000000000000000000");
    }

    public static String getSupportGuildId() {
        return config.get("SUPPORT_GUILD_ID", "0000000000000000000");
    }

    public static long getSupportGuildIdLong() {
        return Long.parseLong(getSupportGuildId());
    }

    public static String getSupportGuildInvite() {
        return config.get("SUPPORT_GUILD_INVITE", "");
    }

    public static String getErrorReportingURL() {
        return config.get("BOT_ERROR_REPORT", "");
    }

    public static String getBotLogoURL() {
        return config.get("BOT_LOGO", "");
    }

    public static String getBotUserAgent() {
        return config.get("BOT_USER_AGENT", "");
    }

    public static String getRepositoryURL() {
        return config.get("REPO_LINK", "");
    }

    public static String getRequestWebhookURL() {
        return config.get("REQUEST_WEBHOOK", "");
    }

    //Database credentials
    public static String getDBUsername() {
        return config.get("DB_USERNAME", "username");
    }

    public static String getDBPassword() {
        return config.get("DB_PASSWORD", "?123456789");
    }

    //Other Tokens
    public static String getOxfordId() {
        return config.get("OXFORD_ID", "");
    }

    public static String getOxfordKey() {
        return config.get("OXFORD_KEY", "");
    }

    public static String getTinyURLToken() {
        return config.get("TINY_URL_TOKEN", "");
    }

    public static String getTinyURlDomain() {
        return config.get("TINY_URL_DOMAIN", "");
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static String getKitsuToken() {
        return config.get("KITSU_TOKEN", "");
    }
}
