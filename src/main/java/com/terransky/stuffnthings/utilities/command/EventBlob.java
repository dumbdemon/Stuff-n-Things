package com.terransky.stuffnthings.utilities.command;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EventBlob {

    private Guild guild;
    private Member member;
    private IInteraction.Type interactionType;

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
     * @see net.dv8tion.jda.api.entities.User#getAsTag()
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

    /**
     * @see Member#getIdLong()
     */
    public long getMemberIdLong() {
        return member.getIdLong();
    }

    /**
     * Get a list of all cached members in a guild that are not a bot.
     *
     * @return A {@link List} of non-bot {@link Member Members}
     */
    public List<Member> getNonBotMembers() {
        return getNonBotMembers(member -> true);
    }

    /**
     * Get a list of all cached members in a guild that are not a bot and some other filter.
     *
     * @param predicate An additional predicate for filtering.
     * @return A {@link List} of non-bot {@link Member Members}
     */
    public List<Member> getNonBotMembers(Predicate<? super Member> predicate) {
        return new ArrayList<>() {{
            guild.getMembers().stream()
                .filter(EventBlob.this::isNotBot)
                .filter(predicate)
                .forEach(this::add);
        }};
    }

    public List<Member> getNonBotMembersAndSelf() {
        return getNonBotMembersAndSelf(member -> true);
    }

    public List<Member> getNonBotMembersAndSelf(Predicate<? super Member> predicate) {
        return new ArrayList<>() {{
            guild.getMembers().stream()
                .filter(member -> isNotBot(member) || getSelfMember().equals(member))
                .filter(predicate)
                .forEach(this::add);
        }};
    }

    /**
     * Checks if the member is a bot.
     *
     * @param member A member to check.
     * @return True if the member is not a bot.
     */
    @Contract("null -> false")
    private boolean isNotBot(Member member) {
        if (member == null)
            return false;
        return !member.getUser().isBot();
    }

    public IInteraction.Type getInteractionType() {
        return interactionType;
    }

    public EventBlob setInteractionType(IInteraction.Type interactionType) {
        this.interactionType = interactionType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBlob eventBlob = (EventBlob) o;
        return getGuild().equals(eventBlob.getGuild()) && getMember().equals(eventBlob.getMember()) && getInteractionType() == eventBlob.getInteractionType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuild(), getMember(), getInteractionType());
    }

    @Override
    public String toString() {
        return "EventBlob{" +
            "guild=" + guild +
            ", member=" + member +
            ", interactionType=" + interactionType +
            '}';
    }
}
