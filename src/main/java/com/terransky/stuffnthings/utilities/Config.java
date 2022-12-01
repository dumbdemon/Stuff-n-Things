package com.terransky.stuffnthings.utilities;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.Contract;

public class Config {

    private static final Dotenv config = Dotenv.configure().load();

    private Config() {
    }

    public static boolean isEnableDatabase() {
        return getConfig().get("ENABLE_DATABASE").equals("true");
    }

    public static boolean isTestingMode() {
        return getConfig().get("TESTING_MODE").equals("true");
    }

    @Contract(pure = true)
    public static Dotenv getConfig() {
        return config;
    }
}
