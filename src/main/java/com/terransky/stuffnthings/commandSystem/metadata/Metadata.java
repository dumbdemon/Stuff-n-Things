package com.terransky.stuffnthings.commandSystem.metadata;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Metadata implements Comparable<Metadata> {
    private String commandName;
    private String longDescription;
    private Mastermind mastermind;
    private Date implementationDate;
    private Date lastUpdated;
    private List<Permission> minPerms = new ArrayList<>();

    public Metadata() {
    }

    public Metadata(String commandName, String longDescription, Mastermind mastermind, Date implementationDate, Date lastUpdated) {
        this(commandName, longDescription, mastermind, implementationDate, lastUpdated, new ArrayList<>());
    }

    public Metadata(String commandName, String longDescription, Mastermind mastermind, Date implementationDate, Date lastUpdated, List<Permission> minPerms) {
        this.commandName = commandName;
        this.longDescription = longDescription;
        this.mastermind = mastermind;
        this.implementationDate = implementationDate;
        this.lastUpdated = lastUpdated;
        this.minPerms = new ArrayList<>(minPerms);
    }

    public void setMinPerms(List<Permission> minPerms) {
        this.minPerms = minPerms;
    }

    public void addToMinPerms(List<Permission> moreMinPerms) {
        minPerms.addAll(moreMinPerms);
    }

    public void addToMinPerms(Permission permission, Permission... permissions) {
        minPerms.add(permission);
        if (permissions != null)
            minPerms.addAll(Arrays.asList(permissions));
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public Mastermind getMastermind() {
        return mastermind;
    }

    public void setMastermind(Mastermind mastermind) {
        this.mastermind = mastermind;
    }

    public Date getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(Date implementationDate) {
        this.implementationDate = implementationDate;
    }

    public long getImplementedAsEpochSecond() {
        return this.getImplementationDate().toInstant().getEpochSecond();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastEditedAsEpochSecond() {
        return this.getLastUpdated().toInstant().getEpochSecond();
    }

    public List<Permission> minPerms() {
        return minPerms;
    }

    public String getLongDescription() {
        int descriptionMaxLength = MessageEmbed.DESCRIPTION_MAX_LENGTH;

        if (longDescription.length() > descriptionMaxLength) {
            return longDescription.substring(0, descriptionMaxLength);
        }

        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return getCommandName().equals(metadata.getCommandName()) && getLongDescription().equals(metadata.getLongDescription()) && getMastermind() == metadata.getMastermind() && getImplementationDate().equals(metadata.getImplementationDate()) && getLastUpdated().equals(metadata.getLastUpdated()) && minPerms.equals(metadata.minPerms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandName(), getLongDescription(), getMastermind(), getImplementationDate(), getLastUpdated(), minPerms);
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.commandName, metadata.commandName);
    }
}
