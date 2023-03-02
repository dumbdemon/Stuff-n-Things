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
public class StuffUser {

    private String name;
    private String id;
    private Long idLong;
    private String discriminator;
    private String avatarId;
    private String avatarUrl;
    private String defaultAvatarId;
    private String defaultAvatarUrl;
    private String effectiveAvatarUrl;
    private String asTag;
    private boolean isBot;
    private boolean isSystem;
    private EnumSet<User.UserFlag> flags;
    private int flagsRaw;

    public StuffUser(User user) {
        Objects.requireNonNull(user);
        this.name = user.getName();
        this.id = user.getId();
        this.idLong = user.getIdLong();
        this.discriminator = user.getDiscriminator();
        this.avatarId = user.getAvatarId();
        this.defaultAvatarId = user.getDefaultAvatarId();
        this.defaultAvatarUrl = user.getDefaultAvatarUrl();
        this.effectiveAvatarUrl = user.getEffectiveAvatarUrl();
        this.asTag = user.getAsTag();
        this.isBot = user.isBot();
        this.isSystem = user.isSystem();
        this.flags = user.getFlags();
        this.flagsRaw = user.getFlagsRaw();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIdLong() {
        return idLong;
    }

    public void setIdLong(Long idLong) {
        this.idLong = idLong;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
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

    public String getAsTag() {
        return asTag;
    }

    public void setAsTag(String asTag) {
        this.asTag = asTag;
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
            getIdLong().equals(stuffUser.getIdLong()) &&
            getDiscriminator().equals(stuffUser.getDiscriminator()) &&
            getAvatarId().equals(stuffUser.getAvatarId()) &&
            Objects.equals(getAvatarUrl(), stuffUser.getAvatarUrl()) &&
            getDefaultAvatarId().equals(stuffUser.getDefaultAvatarId()) &&
            getDefaultAvatarUrl().equals(stuffUser.getDefaultAvatarUrl()) &&
            getEffectiveAvatarUrl().equals(stuffUser.getEffectiveAvatarUrl()) &&
            getAsTag().equals(stuffUser.getAsTag()) &&
            getFlags().equals(stuffUser.getFlags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getIdLong(), getDiscriminator(), getAvatarId(), getAvatarUrl(), getDefaultAvatarId(),
            getDefaultAvatarUrl(), getEffectiveAvatarUrl(), getAsTag(), isBot(), isSystem(), getFlags(), getFlagsRaw());
    }

    @Override
    public String toString() {
        return "StuffUser{" +
            "name='" + name + '\'' +
            ", id='" + id + '\'' +
            ", discriminator='" + discriminator + '\'' +
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
