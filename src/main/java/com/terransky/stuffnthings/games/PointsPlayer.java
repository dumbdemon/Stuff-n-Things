package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.dv8tion.jda.api.entities.Member;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class PointsPlayer<T extends Number> extends Player {

    private T score;

    protected PointsPlayer() {
        super();
    }

    protected PointsPlayer(@NotNull Member member) {
        super(member);
    }

    public T getScore() {
        return score;
    }

    public void setScore(T score) {
        this.score = score;
    }

    /**
     * Add to the player's score.
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
        if (!super.equals(o)) return false;
        PointsPlayer<?> that = (PointsPlayer<?>) o;
        return Objects.equals(getScore(), that.getScore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getScore());
    }
}
