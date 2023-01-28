package com.terransky.stuffnthings.utilities.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class EventBlob {

    private Guild guild;
    private Member member;

    /**
     * An object containing checked non-null {@link Guild} object and {@link Member} object.
     *
     * @param guild  A {@link Guild} from an event.
     * @param member A {@link Member} from an event.
     */
    public EventBlob(Guild guild, Member member) {
        this.guild = guild;
        this.member = member;
    }

    public Guild getGuild() {
        return guild;
    }

    public EventBlob setGuild(Guild guild) {
        this.guild = guild;
        return this;
    }

    /**
     * @see Guild#getName()
     */
    public String getGuildName() {
        return guild.getName();
    }

    /**
     * @see Guild#getId()
     */
    public String getGuildId() {
        return guild.getId();
    }

    /**
     * @see Guild#getIdLong()
     */
    public long getGuildIdLong() {
        return guild.getIdLong();
    }

    public Member getSelfMember() {
        return guild.getSelfMember();
    }

    public Member getMember() {
        return member;
    }

    public EventBlob setMember(Member member) {
        this.member = member;
        return this;
    }

    /**
     * @see Member#getEffectiveAvatarUrl()
     */
    public String getMemberEffectiveAvatarUrl() {
        return member.getEffectiveAvatarUrl();
    }

    /**
     * @see User#getAsTag()
     */
    public String getMemberAsTag() {
        return member.getUser().getAsTag();
    }

    /**
     * @see Member#getId()
     */
    public String getMemberId() {
        return member.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBlob blob = (EventBlob) o;
        return getGuild().equals(blob.getGuild()) && getMember().equals(blob.getMember());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuild(), getMember());
    }

    @Override
    public String toString() {
        return "EventBlob{" +
            "guild=" + guild +
            ", member=" + member +
            '}';
    }
}
