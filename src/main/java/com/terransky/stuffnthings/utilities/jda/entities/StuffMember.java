package com.terransky.stuffnthings.utilities.jda.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({
    "timeBoosted", "boosting", "timedOut", "nickname", "effectiveName", "id", "idLong", "discriminator", "memberAsTag",
    "bot", "system", "avatarId", "effectiveAvatarUrl", "roles", "roleIds", "owner", "flags", "flagsRaw"
})
@SuppressWarnings("unused")
public class StuffMember extends StuffEntity {

    private StuffUser user;
    private String timeBoosted;
    private boolean isBoosting;
    private boolean isTimedOut;
    private String nickname;
    private String effectiveName;
    private String avatarId;
    private String effectiveAvatarUrl;
    private List<String> roles;
    private List<Long> roleIds;
    private boolean isOwner;

    public StuffMember(Member member) {
        Objects.requireNonNull(member);
        this.user = new StuffUser(member.getUser());
        this.timeBoosted = member.getTimeBoosted() == null ? "N/A" : member.getTimeBoosted().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.isBoosting = member.isBoosting();
        this.isTimedOut = member.isTimedOut();
        this.nickname = member.getNickname();
        this.effectiveName = member.getEffectiveName();
        setId(member.getId());
        setIdLong(member.getIdLong());
        this.avatarId = member.getAvatarId();
        this.effectiveAvatarUrl = member.getEffectiveAvatarUrl();
        this.roles = member.getRoles().stream().map(Role::getName).toList();
        this.roleIds = member.getRoles().stream().map(Role::getIdLong).toList();
        this.isOwner = member.isOwner();
    }

    public StuffUser getUser() {
        return user;
    }

    public void setUser(StuffUser user) {
        this.user = user;
    }

    public String getTimeBoosted() {
        return timeBoosted;
    }

    public void setTimeBoosted(String timeBoosted) {
        this.timeBoosted = timeBoosted;
    }

    public boolean isBoosting() {
        return isBoosting;
    }

    public void setBoosting(boolean boosting) {
        isBoosting = boosting;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public void setTimedOut(boolean timedOut) {
        isTimedOut = timedOut;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEffectiveName() {
        return effectiveName;
    }

    public void setEffectiveName(String effectiveName) {
        this.effectiveName = effectiveName;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getEffectiveAvatarUrl() {
        return effectiveAvatarUrl;
    }

    public void setEffectiveAvatarUrl(String effectiveAvatarUrl) {
        this.effectiveAvatarUrl = effectiveAvatarUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StuffMember that = (StuffMember) o;
        return isTimedOut() == that.isTimedOut() &&
            getUser().equals(that.getUser()) &&
            Objects.equals(getTimeBoosted(), that.getTimeBoosted()) &&
            getNickname().equals(that.getNickname()) &&
            getEffectiveName().equals(that.getEffectiveName()) &&
            getId().equals(that.getId()) &&
            getEffectiveAvatarUrl().equals(that.getEffectiveAvatarUrl()) &&
            getRoleIds().equals(that.getRoleIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getTimeBoosted(), isBoosting(), isTimedOut(), getNickname(), getEffectiveName(), getId(),
            getIdLong(), getAvatarId(), getEffectiveAvatarUrl(), getRoles(), getRoleIds(), isOwner());
    }

    @Override
    public String toString() {
        return "StuffMember{" +
            "user=" + user +
            ", timeBoosted='" + timeBoosted + '\'' +
            ", isBoosting=" + isBoosting +
            ", isTimedOut=" + isTimedOut +
            ", nickname='" + nickname + '\'' +
            ", effectiveName='" + effectiveName + '\'' +
            ", avatarId='" + avatarId + '\'' +
            ", effectiveAvatarUrl='" + effectiveAvatarUrl + '\'' +
            ", roles=" + roles +
            ", roleIds=" + roleIds +
            ", isOwner=" + isOwner +
            '}';
    }
}
