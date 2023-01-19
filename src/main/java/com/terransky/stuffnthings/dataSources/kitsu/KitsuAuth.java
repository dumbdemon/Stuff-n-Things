package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Generated;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "access_token",
    "created_at",
    "expires_in",
    "refresh_token",
    "scope",
    "token_type"
})
@Generated("jsonschema2pojo")
public class KitsuAuth {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Token used in Authorization header
     */
    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(long createdAt) {
        this.createdAt = new Date(TimeUnit.SECONDS.toMillis(createdAt));
    }

    public LocalDate getExpiredAtAsLocalDate() {
        return getExpiresAt().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    /**
     * Seconds until the access_token expires (30 days default)
     */
    @JsonProperty("expires_in")
    public Long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("expires_in")
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Date getExpiresAt() {
        return new Date(createdAt.getTime() + TimeUnit.SECONDS.toMillis(expiresIn));
    }

    public long getDaysUntilExpired() {
        return LocalDate.now().until(getExpiredAtAsLocalDate(), ChronoUnit.DAYS);
    }

    public boolean isExpired() {
        return getDaysUntilExpired() < 0;
    }

    /**
     * Token used to get a new access_token
     */
    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonProperty("refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("scope")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getPrettyString(@NotNull ObjectMapper mapper) {
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("access_token", accessToken);
        rootNode.put("created_at", TimeUnit.MILLISECONDS.toSeconds(createdAt.getTime()));
        rootNode.put("expires_in", expiresIn);
        rootNode.put("refresh_token", refreshToken);
        rootNode.put("scope", scope);
        rootNode.put("token_type", tokenType);

        return rootNode.toPrettyString();
    }
}
