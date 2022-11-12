package com.terransky.StuffnThings.commandSystem.Metadata;

public enum Mastermind {
    USER("User"),
    DEVELOPER("Developer"),
    DEFAULT("Bot Standard");

    final String who;

    Mastermind(String who) {
        this.who = who;
    }

    public String getWho() {
        return who;
    }
}
