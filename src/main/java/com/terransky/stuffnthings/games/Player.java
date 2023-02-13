package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.dv8tion.jda.api.entities.User;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Player to represent a player
 *
 * @param <T> A Number type as each game may or may not require a decimal
 */
@SuppressWarnings("unused")
public class Player<T extends Number> {

    private String name;
    private String id;
    private String mention;
    private T score;

    /**
     * Constructor for a new Player
     *
     * @param user A {@link User} to create the Player object for
     */
    protected Player(@NotNull User user) {
        this.name = user.getName();
        this.id = user.getId();
        this.mention = user.getAsMention();
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

    public void setMention(String mention) {
        this.mention = mention;
    }

    public T getScore() {
        return score;
    }

    public void setScore(T score) {
        this.score = score;
    }

    /**
     * Add to the player's score if the game has a scoring system.
     * This function <b>must</b> be overridden or else it returns null.
     *
     * @param toAdd The amount to add
     * @return The player's new score
     */
    @JsonIgnore
    @BsonIgnore
    public T addToScore(T toAdd) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player<?> player = (Player<?>) o;
        return getName().equals(player.getName()) && getId().equals(player.getId()) && getMention().equals(player.getMention()) && Objects.equals(getScore(), player.getScore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId(), getMention(), getScore());
    }
}
