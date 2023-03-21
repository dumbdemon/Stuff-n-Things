package com.terransky.stuffnthings.utilities.command;

import java.awt.*;

public enum EmbedColor {

    DEFAULT(102, 51, 102),
    SUB_DEFAULT(153, 102, 153),
    ERROR(153, 0, 102);

    private final Color color;

    EmbedColor(int r, int g, int b) {
        this.color = new Color(r, g, b);
    }

    public Color getColor() {
        return color;
    }
}
