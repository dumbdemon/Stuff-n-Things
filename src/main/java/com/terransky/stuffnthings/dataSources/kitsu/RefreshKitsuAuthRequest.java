package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * POJO to request a token refresh
 */
@JsonPropertyOrder({
    "grant_type",
    "refresh_token"
})
@SuppressWarnings("unused")
public class RefreshKitsuAuthRequest extends KitsuAuthRequest {

    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Constructor to request an api token refresh
     */
    public RefreshKitsuAuthRequest() {
        super(GrantType.REFRESH);
    }

    /**
     * Get the refresh token
     *
     * @return String containing the refresh token
     */
    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Set the refresh token
     *
     * @param refreshToken the refresh token
     * @return the instance for chaining
     */
    @JsonProperty("refresh_token")
    public RefreshKitsuAuthRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
