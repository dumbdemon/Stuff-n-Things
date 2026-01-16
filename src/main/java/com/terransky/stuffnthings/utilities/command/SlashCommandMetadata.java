package com.terransky.stuffnthings.utilities.command;

import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class SlashCommandMetadata implements Comparable<SlashCommandMetadata> {

    private final String name;
    private final String description;
    private final Mastermind mastermind;
    private final CommandCategory category;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
    private final boolean isNSFW;

    public SlashCommandMetadata(@NotNull SlashCommandInteraction interaction) {
        this.name = interaction.getNameReadable();
        this.description = interaction.getDescription();
        this.mastermind = interaction.getMastermind();
        this.category = interaction.getCategory();
        this.createdAt = interaction.getCreatedAt();
        this.updatedAt = interaction.getUpdatedAt();
        this.isNSFW = interaction.isNSFW();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Mastermind getMastermind() {
        return mastermind;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    @Override
    public int compareTo(@NotNull SlashCommandMetadata o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), o.getName());
    }
}
