package com.terransky.stuffnthings.utilities.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EmbedColors {

    private EmbedColors() {
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Color getDefault() {
        return new Color(102, 51, 102);
    }

    public static int getDefaultRGB() {
        return getDefault().getRGB();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Color getSecondary() {
        return new Color(153, 102, 153);
    }

    public static int getSecondaryRGB() {
        return getSecondary().getRGB();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Color getError() {
        return new Color(153, 0, 102);
    }
}
