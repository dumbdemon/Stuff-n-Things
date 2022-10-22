package com.terransky.StuffnThings.exceptions;

public class DiscordAPIException extends Exception {
    public DiscordAPIException(String errorMessage) {
        super(errorMessage);
    }
}
