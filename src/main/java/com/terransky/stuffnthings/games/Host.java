package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;

@JsonPropertyOrder({
    "hostId",
    "requiredPermissions"
})
@SuppressWarnings("unused")
public class Host {

    private String hostId;
    private String hostMention;

    public Host() {
    }

    public Host(Member member) {
        Objects.requireNonNull(member);
        this.hostId = member.getId();
        this.hostMention = member.getAsMention();
    }

    public String getHostId() {
        return hostId;
    }

    public Host setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    public String getHostMention() {
        return hostMention;
    }

    public void setHostMention(String hostMention) {
        this.hostMention = hostMention;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return getHostId().equals(host.getHostId()) &&
            getHostMention().equals(host.getHostMention());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHostId(), getHostMention());
    }

    @Override
    public String toString() {
        return "Host{" +
            "hostId='" + hostId + '\'' +
            ", hostMention='" + hostMention + '\'' +
            '}';
    }
}
