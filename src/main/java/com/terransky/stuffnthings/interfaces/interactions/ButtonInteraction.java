package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.general.MathicTools;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;

import java.util.Random;
import java.util.regex.Pattern;

public abstract class ButtonInteraction implements IInteraction.IButton {

    private final String name;
    private final Pattern pagePattern;

    protected ButtonInteraction(String name) {
        this(name, null);
    }

    public String getButtonId(int page, String identifier) {
        return "";
    }

    protected ButtonInteraction(String name, Pattern pagePattern) {
        this.name = name;
        this.pagePattern = pagePattern;
    }

    private String getCustomId(boolean disabled) {
        return getCustomId(getName(), disabled);
    }

    private String getCustomId(String id, boolean disabled) {
        return disabled ? "disabled-button" + new Random(MathicTools.getStringAsSeed(getName())).nextInt(512) : id;
    }

    public Button getButton(ButtonStyle style, String label) {
        return getButton(style, label, false);
    }

    @SuppressWarnings("unused")
    public Button getButton(String label) {
        return getButton(ButtonStyle.PRIMARY, label);
    }

    public Button getButton(ButtonStyle style, String label, boolean disabled) {
        return new ButtonImpl(getCustomId(disabled), label, style, disabled, null);
    }

    public Button getButton(String label, boolean disabled) {
        return getButton(ButtonStyle.PRIMARY, label, disabled);
    }

    public Button getButton(ButtonStyle style, int page, String identifier, String label, boolean disabled) {
        return new ButtonImpl(getCustomId(getButtonId(page, identifier), disabled), label, style, disabled, null);
    }
    public Button getButton(ButtonStyle style, int page, String identifier, String label) {
        return getButton(style, page, identifier, label, false);
    }

    public Button getButton(int page, String identifier, String label, boolean disabled) {
        return getButton(ButtonStyle.PRIMARY, page, identifier, label, disabled);
    }
    public Button getButton(int page, String identifier, String label) {
        return getButton(ButtonStyle.PRIMARY, page, identifier, label);
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
