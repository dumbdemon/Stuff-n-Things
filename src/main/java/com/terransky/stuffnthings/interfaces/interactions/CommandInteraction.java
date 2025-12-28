package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class CommandInteraction<T extends GenericCommandInteractionEvent> implements IInteraction.ICommand<T> {

    private final String name;
    private final List<Long> restrictedServers = new ArrayList<>();
    private boolean isWorking = true;
    private boolean isOwnerOnly = false;
    private boolean isDeveloperOnly = false;
    private String disabledReason = "";
    private final List<Permission> defaultMemberPermissions = new ArrayList<>();

    protected CommandInteraction(String name) {
        this.name = name;
    }

    public boolean isWorking() {
        return isWorking;
    }

    protected void setWorking(boolean working) {
        isWorking = working;
    }

    public boolean isDisabled() {
        return !disabledReason.isEmpty();
    }

    public boolean isGuildPrivate() {
        return !getRestrictedServers().isEmpty();
    }

    public boolean isOwnerOnly() {
        return isOwnerOnly;
    }

    protected void setOwnerOnly() {
        isOwnerOnly = true;
    }

    public boolean isDeveloperOnly() {
        return isDeveloperOnly;
    }

    protected void setDeveloperOnly() {
        isDeveloperOnly = true;
    }

    protected void setDeveloperOnly(boolean developerOnly) {
        isDeveloperOnly = developerOnly;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    protected void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public CommandData getCommandData() {
        return null;
    }

    public List<Long> getRestrictedServers() {
        return List.copyOf(restrictedServers);
    }

    protected void addRestrictedServer(List<Long> restrictedServers) {
        this.restrictedServers.addAll(restrictedServers);
    }

    protected void addRestrictedServer(long serverId) {
        addRestrictedServer(List.of(serverId));
    }

    public List<Permission> getDefaultMemberPermissions() {
        return defaultMemberPermissions;
    }

    protected void setDefaultMemberPermissions(Permission... defaultMemberPermissions) {
        this.defaultMemberPermissions.addAll(Arrays.asList(defaultMemberPermissions));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract void execute(@NotNull T event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException;
}
