package com.terransky.stuffnthings;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.Contract;

import java.awt.*;

public class Commons {
    public static final Color DEFAULT_EMBED_COLOR = new Color(102, 51, 102);
    public static final Color SECONDARY_EMBED_COLOR = new Color(153, 102, 153);
    public static final Dotenv CONFIG = Dotenv.configure().load();
    public static final boolean IS_TESTING_MODE = CONFIG.get("TESTING_MODE").equals("true");
    public static final boolean ENABLE_DATABASE = CONFIG.get("ENABLE_DATABASE").equals("true");

    @Contract(pure = true)
    private Commons() {
    }
}
