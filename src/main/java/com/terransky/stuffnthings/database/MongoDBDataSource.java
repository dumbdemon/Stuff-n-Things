package com.terransky.stuffnthings.database;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.DBProperty;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class MongoDBDataSource implements DatabaseManager { //todo: Set this up

    @Override
    public Optional<Integer> getFromDBInt(@NotNull EventBlob blob, @NotNull DBProperty property) {
        return Optional.of(0);
    }

    @Override
    public Optional<String> getFromDBString(@NotNull EventBlob blob, @NotNull DBProperty property) {
        return Optional.empty();
    }

    @Override
    public boolean getFromDBBoolean(@NotNull EventBlob blob, @NotNull DBProperty property) {
        return false;
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, int newValue) {
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, String newValue) {
    }

    @Override
    public void updateProperty(@NotNull EventBlob blob, @NotNull DBProperty property, boolean newValue) {
    }

    @Override
    public void addGuild(@NotNull Guild guild) {
    }

    @Override
    public void removeGuild(@NotNull Guild guild) {
    }

    @Override
    public void addUser(@NotNull EventBlob blob) {
    }

    @Override
    public void removeUser(String userID, Guild guild) {
    }
}
