package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * POJO to request a new password based token
 */
@JsonPropertyOrder({
    "grant_type",
    "username",
    "password"
})
@SuppressWarnings("unused")
public class PasswordKitsuAuthForm extends KitsuAuthForm {

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    /**
     * Constructor for a password based request to obtain an API token from Kitsu.io
     */
    public PasswordKitsuAuthForm() {
        super(GrantType.PASSWORD);
    }

    /**
     * Get the username
     *
     * @return String containing the username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username a username
     * @return the instance for chaining
     */
    @JsonProperty("username")
    public PasswordKitsuAuthForm setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Get the password
     *
     * @return String containing the password
     */
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     *
     * @param password a password
     * @return the instance for chaining
     */
    @JsonProperty("password")
    public PasswordKitsuAuthForm setPassword(String password) {
        this.password = password;
        return this;
    }
}
