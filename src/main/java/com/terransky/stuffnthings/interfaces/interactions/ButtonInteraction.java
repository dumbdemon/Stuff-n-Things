package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;

import java.util.regex.Pattern;

public abstract class ButtonInteraction implements IInteraction.IButton {

    private final String name;
    private final Pattern pagePattern;
    public static final String DISABLED_BUTTON_ID = "disabled-button";

    protected ButtonInteraction(String name) {
        this(name, null);
    }

    public String getButtonId(int page) {
        return "";
    }

    protected ButtonInteraction(String name, Pattern pagePattern) {
        this.name = name;
        this.pagePattern = pagePattern;
    }

    public Button getButton(ButtonStyle style, String label) {
        return getButton(style, label, false);
    }

    public Button getButton(ButtonStyle style, String label, boolean disabled) {
        return new ButtonImpl(getName(), label, style, disabled, null);
    }

    public boolean followsPattern(String name) {
        if (pagePattern == null)
            return false;

        return pagePattern.matcher(name).matches();
    }

    @Override
    public String getName() {
        return name;
    }
}
