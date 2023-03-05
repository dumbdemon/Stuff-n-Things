package com.terransky.stuffnthings.utilities.command;

import java.awt.*;

public enum EmbedColor {

    DEFAULT(new Color(102, 51, 102)),
    SUB_DEFAULT(new Color(153, 102, 153)),
    ERROR(new Color(153, 0, 102));

    private final Color color;

    EmbedColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
