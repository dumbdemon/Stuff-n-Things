package com.terransky.stuffnthings.games;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Player to represent a player
 */
public class Player implements Comparable<Player> {

    private String name;
    private String id;
    private String mention;

    /**
     * Constructor Jackson and MongoDB
     */
    protected Player() {
    }

    /**
     * Constructor for a new Player
     *
     * @param member A {@link Member} to create the Player object for
     */
    protected Player(@NotNull Member member) {
        this.name = member.getEffectiveName();
        this.id = member.getId();
        this.mention = member.getAsMention();
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

    public String getMention() {
        return mention;
    }

    @SuppressWarnings("unused")
    public void setMention(String mention) {
        this.mention = mention;
    }

    /**
     * Checks if the player has won.<br/>
     * This function is meant to be overridden; otherwise, it returns false.
     *
     * @return True if the player has won
     */
    public boolean checkWinner() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return getName().equals(player.getName()) &&
            getId().equals(player.getId()) &&
            getMention().equals(player.getMention());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getMention());
    }

    @Override
    public int compareTo(@NotNull Player player) {
        return String.CASE_INSENSITIVE_ORDER.compare(getName(), player.getName());
    }
}
