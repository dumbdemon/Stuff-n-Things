package com.terransky.stuffnthings.utilities.jda.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.dv8tion.jda.api.entities.User;

import java.util.EnumSet;
import java.util.Objects;

@JsonPropertyOrder({
    "name", "id", "idLong", "discriminator", "avatarId", "avatarUrl", "defaultAvatarId",
    "defaultAvatarUrl", "effectiveAvatarUrl", "asTag", "bot", "system", "flags", "flagsRaw"
})
@SuppressWarnings("unused")
public class StuffUser extends StuffShared {

    private String avatarId;
    private String avatarUrl;
    private String defaultAvatarId;
    private String defaultAvatarUrl;
    private String effectiveAvatarUrl;
    private final String asTag;
    private boolean isBot;
    private boolean isSystem;
    private EnumSet<User.UserFlag> flags;
    private int flagsRaw;

    public StuffUser(User user) {
        Objects.requireNonNull(user);
        setName(user.getName());
        setId(user.getId());
        setIdLong(user.getIdLong());
        this.avatarId = user.getAvatarId();
        this.defaultAvatarId = user.getDefaultAvatarId();
        this.defaultAvatarUrl = user.getDefaultAvatarUrl();
        this.effectiveAvatarUrl = user.getEffectiveAvatarUrl();
        this.asTag = user.getName();
        this.isBot = user.isBot();
        this.isSystem = user.isSystem();
        this.flags = user.getFlags();
        this.flagsRaw = user.getFlagsRaw();
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDefaultAvatarId() {
        return defaultAvatarId;
    }

    public void setDefaultAvatarId(String defaultAvatarId) {
        this.defaultAvatarId = defaultAvatarId;
    }

    public String getDefaultAvatarUrl() {
        return defaultAvatarUrl;
    }

    public void setDefaultAvatarUrl(String defaultAvatarUrl) {
        this.defaultAvatarUrl = defaultAvatarUrl;
    }

    public String getEffectiveAvatarUrl() {
        return effectiveAvatarUrl;
    }

    public void setEffectiveAvatarUrl(String effectiveAvatarUrl) {
        this.effectiveAvatarUrl = effectiveAvatarUrl;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public EnumSet<User.UserFlag> getFlags() {
        return flags;
    }

    public void setFlags(EnumSet<User.UserFlag> flags) {
        this.flags = flags;
    }

    public int getFlagsRaw() {
        return flagsRaw;
    }

    public void setFlagsRaw(int flagsRaw) {
        this.flagsRaw = flagsRaw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StuffUser stuffUser = (StuffUser) o;
        return isBot() == stuffUser.isBot() &&
            isSystem() == stuffUser.isSystem() &&
            getFlagsRaw() == stuffUser.getFlagsRaw() &&
            getName().equals(stuffUser.getName()) &&
            getId().equals(stuffUser.getId()) &&
            getDefaultAvatarId().equals(stuffUser.getDefaultAvatarId()) &&
            getEffectiveAvatarUrl().equals(stuffUser.getEffectiveAvatarUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getIdLong(), getAvatarId(), getAvatarUrl(), getDefaultAvatarId(),
            getDefaultAvatarUrl(), getEffectiveAvatarUrl(), isBot(), isSystem(), getFlags(), getFlagsRaw());
    }

    @Override
    public String toString() {
        return "StuffUser{" +
            ", avatarId='" + avatarId + '\'' +
            ", avatarUrl='" + avatarUrl + '\'' +
            ", defaultAvatarId='" + defaultAvatarId + '\'' +
            ", defaultAvatarUrl='" + defaultAvatarUrl + '\'' +
            ", effectiveAvatarUrl='" + effectiveAvatarUrl + '\'' +
            ", asTag='" + asTag + '\'' +
            ", isBot=" + isBot +
            ", isSystem=" + isSystem +
            ", flags=" + flags +
            ", flagsRaw=" + flagsRaw +
            '}';
    }
}
