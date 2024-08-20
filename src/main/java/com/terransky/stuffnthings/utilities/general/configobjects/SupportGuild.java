package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class SupportGuild {

    private Long id;
    private String invite;

    SupportGuild() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvite() {
        return invite;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }
}
