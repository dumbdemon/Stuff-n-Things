package com.terransky.stuffnthings.games;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.dv8tion.jda.api.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({
    "hostId",
    "requiredPermissions"
})
@SuppressWarnings("unused")
public class Host {

    private String hostId;
    private Collection<Permission> requiredPermissions;

    public Host() {
    }

    public Host(String hostId, Permission... permissions) {
        this(hostId, List.of(permissions));
    }

    public Host(String hostId, Collection<Permission> permissions) {
        this.hostId = hostId;
        this.requiredPermissions = List.copyOf(permissions);
    }

    public String getHostId() {
        return hostId;
    }

    public Host setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    public Collection<Permission> getRequiredPermissions() {
        return requiredPermissions;
    }

    public Host setRequiredPermissions(List<Permission> requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return getHostId().equals(host.getHostId()) && getRequiredPermissions().equals(host.getRequiredPermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHostId(), getRequiredPermissions());
    }

    @Override
    public String toString() {
        return "GameHost{" +
            "hostId='" + hostId + '\'' +
            ", requiredPermissions=" + requiredPermissions +
            '}';
    }
}
