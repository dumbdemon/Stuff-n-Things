package com.terransky.stuffnthings.utilities.jda.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonPropertyOrder({
    "loaded", "memberCount", "name", "id", "idLong", "iconId", "iconUrl", "features", "invitesDisabled", "splashId", "splashUrl", "vanityCode", "vanityUrl",
    "description", "bannerId", "bannerUrl", "boostTier", "boostCount", "boosters", "maxBitRate", "maxFileSize", "maxMembers", "owner", "ownerIdLong", "ownerId",
    "nsfwLevel", "members", "verificationLevel", "defaultNotificationLevel", "requiredMFALevel", "explicitContentLevel"
})

@SuppressWarnings("unused")
public class StuffGuild extends StuffShared {

    private boolean isLoaded;
    private int memberCount;
    private String iconId;
    private String iconUrl;
    private Set<String> features;
    private boolean isInvitesDisabled;
    private String splashId;
    private String splashUrl;
    private String vanityCode;
    private String vanityUrl;
    private String description;
    private String bannerId;
    private String bannerUrl;
    private Guild.BoostTier boostTier;
    private int boostCount;
    private int maxBitRate;
    private long maxFileSize;
    private int maxMembers;
    private StuffMember owner;
    private long ownerIdLong;
    private String ownerId;
    private Guild.NSFWLevel nsfwLevel;
    private List<String> roles;
    private boolean isBoostProgressBarEnabled;
    private Guild.VerificationLevel verificationLevel;
    private Guild.NotificationLevel defaultNotificationLevel;
    private Guild.MFALevel requiredMFALevel;
    private Guild.ExplicitContentLevel explicitContentLevel;

    public StuffGuild(Guild guild) {
        Objects.requireNonNull(guild);
        this.isLoaded = guild.isLoaded();
        this.memberCount = guild.getMemberCount();
        setName(guild.getName());
        setId(guild.getId());
        setIdLong(guild.getIdLong());
        this.iconId = guild.getIconId();
        this.iconUrl = guild.getIconUrl();
        this.features = guild.getFeatures();
        this.isInvitesDisabled = guild.isInvitesDisabled();
        this.splashId = guild.getSplashId();
        this.splashUrl = guild.getSplashUrl();
        this.vanityCode = guild.getVanityCode();
        this.vanityUrl = guild.getVanityUrl();
        this.description = guild.getDescription();
        this.bannerId = guild.getBannerId();
        this.bannerUrl = guild.getBannerUrl();
        this.boostTier = guild.getBoostTier();
        this.boostCount = guild.getBoostCount();
        this.maxBitRate = guild.getMaxBitrate();
        this.maxFileSize = guild.getMaxFileSize();
        this.maxMembers = guild.getMaxMembers();
        this.owner = new StuffMember(guild.getOwner());
        this.ownerIdLong = guild.getOwnerIdLong();
        this.ownerId = guild.getOwnerId();
        this.nsfwLevel = guild.getNSFWLevel();
        this.roles = guild.getRoles().stream().map(Role::getName).toList();
        this.isBoostProgressBarEnabled = guild.isBoostProgressBarEnabled();
        this.verificationLevel = guild.getVerificationLevel();
        this.defaultNotificationLevel = guild.getDefaultNotificationLevel();
        this.requiredMFALevel = guild.getRequiredMFALevel();
        this.explicitContentLevel = guild.getExplicitContentLevel();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Set<String> getFeatures() {
        return features;
    }

    public void setFeatures(Set<String> features) {
        this.features = Set.copyOf(features);
    }

    public boolean isInvitesDisabled() {
        return isInvitesDisabled;
    }

    public void setInvitesDisabled(boolean invitesDisabled) {
        isInvitesDisabled = invitesDisabled;
    }

    public String getSplashId() {
        return splashId;
    }

    public void setSplashId(String splashId) {
        this.splashId = splashId;
    }

    public String getSplashUrl() {
        return splashUrl;
    }

    public void setSplashUrl(String splashUrl) {
        this.splashUrl = splashUrl;
    }

    public String getVanityCode() {
        return vanityCode;
    }

    public void setVanityCode(String vanityCode) {
        this.vanityCode = vanityCode;
    }

    public String getVanityUrl() {
        return vanityUrl;
    }

    public void setVanityUrl(String vanityUrl) {
        this.vanityUrl = vanityUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public Guild.BoostTier getBoostTier() {
        return boostTier;
    }

    public void setBoostTier(Guild.BoostTier boostTier) {
        this.boostTier = boostTier;
    }

    public int getBoostCount() {
        return boostCount;
    }

    public void setBoostCount(int boostCount) {
        this.boostCount = boostCount;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public void setMaxBitRate(int maxBitRate) {
        this.maxBitRate = maxBitRate;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public StuffMember getOwner() {
        return owner;
    }

    public void setOwner(StuffMember owner) {
        this.owner = owner;
    }

    public long getOwnerIdLong() {
        return ownerIdLong;
    }

    public void setOwnerIdLong(long ownerIdLong) {
        this.ownerIdLong = ownerIdLong;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Guild.NSFWLevel getNsfwLevel() {
        return nsfwLevel;
    }

    public void setNsfwLevel(Guild.NSFWLevel nsfwLevel) {
        this.nsfwLevel = nsfwLevel;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = List.copyOf(roles);
    }

    public boolean isBoostProgressBarEnabled() {
        return isBoostProgressBarEnabled;
    }

    public void setBoostProgressBarEnabled(boolean boostProgressBarEnabled) {
        isBoostProgressBarEnabled = boostProgressBarEnabled;
    }

    public Guild.VerificationLevel getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(String verificationLevel) {
        if (verificationLevel == null) {
            this.verificationLevel = Guild.VerificationLevel.UNKNOWN;
            return;
        }

        switch (verificationLevel) {
            case "NONE" -> this.verificationLevel = Guild.VerificationLevel.NONE;
            case "LOW" -> this.verificationLevel = Guild.VerificationLevel.LOW;
            case "MEDIUM" -> this.verificationLevel = Guild.VerificationLevel.MEDIUM;
            case "HIGH" -> this.verificationLevel = Guild.VerificationLevel.HIGH;
            case "VERY_HIGH" -> this.verificationLevel = Guild.VerificationLevel.VERY_HIGH;
        }
    }

    public Guild.NotificationLevel getDefaultNotificationLevel() {
        return defaultNotificationLevel;
    }

    public void setDefaultNotificationLevel(String defaultNotificationLevel) {
        if (defaultNotificationLevel == null) {
            this.defaultNotificationLevel = Guild.NotificationLevel.UNKNOWN;
            return;
        }

        switch (defaultNotificationLevel) {
            case "ALL_MESSAGES" -> this.defaultNotificationLevel = Guild.NotificationLevel.ALL_MESSAGES;
            case "MENTIONS_ONLY" -> this.defaultNotificationLevel = Guild.NotificationLevel.MENTIONS_ONLY;
        }
    }

    public Guild.MFALevel getRequiredMFALevel() {
        return requiredMFALevel;
    }

    public void setRequiredMFALevel(String requiredMFALevel) {
        if (requiredMFALevel == null) {
            this.requiredMFALevel = Guild.MFALevel.UNKNOWN;
            return;
        }

        switch (requiredMFALevel) {
            case "NONE" -> this.requiredMFALevel = Guild.MFALevel.NONE;
            case "TWO_FACTOR_AUTH" -> this.requiredMFALevel = Guild.MFALevel.TWO_FACTOR_AUTH;
        }
    }

    public Guild.ExplicitContentLevel getExplicitContentLevel() {
        return explicitContentLevel;
    }

    public void setExplicitContentLevel(String explicitContentLevel) {
        if (explicitContentLevel == null) {
            this.explicitContentLevel = Guild.ExplicitContentLevel.UNKNOWN;
            return;
        }

        switch (explicitContentLevel) {
            case "OFF" -> this.explicitContentLevel = Guild.ExplicitContentLevel.OFF;
            case "NO_ROLE" -> this.explicitContentLevel = Guild.ExplicitContentLevel.NO_ROLE;
            case "ALL" -> this.explicitContentLevel = Guild.ExplicitContentLevel.ALL;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StuffGuild that = (StuffGuild) o;
        return getMemberCount() == that.getMemberCount() &&
            getBoostCount() == that.getBoostCount() &&
            getName().equals(that.getName()) &&
            getId().equals(that.getId()) &&
            getFeatures().equals(that.getFeatures()) &&
            getBoostTier() == that.getBoostTier() &&
            getOwner().equals(that.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isLoaded(), getMemberCount(), getName(), getId(), getIdLong(), getIconId(), getIconUrl(), getFeatures(),
            isInvitesDisabled(), getSplashId(), getSplashUrl(), getVanityCode(), getVanityUrl(), getDescription(), getBannerId(),
            getBannerUrl(), getBoostTier(), getBoostCount(), getMaxBitRate(), getMaxFileSize(), getMaxMembers(), getOwner(),
            getOwnerIdLong(), getOwnerId(), getNsfwLevel(), getRoles(), isBoostProgressBarEnabled(), getVerificationLevel(),
            getDefaultNotificationLevel(), getRequiredMFALevel(), getExplicitContentLevel());
    }

    @Override
    public String toString() {
        return "StuffGuild{" +
            "isLoaded=" + isLoaded +
            ", memberCount=" + memberCount +
            ", name='" + getName() + '\'' +
            ", id='" + getId() + '\'' +
            ", iconId='" + iconId + '\'' +
            ", iconUrl='" + iconUrl + '\'' +
            ", features=" + features +
            ", isInvitesDisabled=" + isInvitesDisabled +
            ", splashId='" + splashId + '\'' +
            ", splashUrl='" + splashUrl + '\'' +
            ", vanityCode='" + vanityCode + '\'' +
            ", vanityUrl='" + vanityUrl + '\'' +
            ", description='" + description + '\'' +
            ", bannerId='" + bannerId + '\'' +
            ", bannerUrl='" + bannerUrl + '\'' +
            ", boostTier=" + boostTier +
            ", boostCount=" + boostCount +
            ", maxBitRate=" + maxBitRate +
            ", maxFileSize=" + maxFileSize +
            ", maxMembers=" + maxMembers +
            ", owner=" + owner +
            ", ownerIdLong=" + ownerIdLong +
            ", ownerId='" + ownerId + '\'' +
            ", nsfwLevel=" + nsfwLevel +
            ", roles=" + roles +
            ", isBoostProgressBarEnabled=" + isBoostProgressBarEnabled +
            ", verificationLevel=" + verificationLevel +
            ", defaultNotificationLevel=" + defaultNotificationLevel +
            ", requiredMFALevel=" + requiredMFALevel +
            ", explicitContentLevel=" + explicitContentLevel +
            '}';
    }
}
