package com.terransky.stuffnthings.utilities;

public enum Mastermind {
    USER("User"),
    DEVELOPER("Developer"),
    DEFAULT("Bot Standard");

    private final String who;

    Mastermind(String who) {
        this.who = who;
    }

    public String getWho() {
        return who;
    }
}
