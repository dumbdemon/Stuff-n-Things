package com.terransky.StuffnThings;

import java.awt.*;

public class Commons {
    public final Color defaultEmbedColor = new Color(102, 51, 102);
    public final Color secondaryEmbedColor = new Color(153, 77, 153);

    public int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

}
