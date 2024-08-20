package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class UserPassword {
    private String username;
    private String password;

    UserPassword() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmpty() {
        return getUsername() == null | getPassword() == null;
    }
}
