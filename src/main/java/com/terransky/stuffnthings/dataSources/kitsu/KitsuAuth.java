package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

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
public class KitsuAuth implements Pojo {

    @JsonProperty("access_token")
    @BsonProperty("access_token")
    private String accessToken;
    @JsonProperty("created_at")
    @BsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("expires_in")
    @BsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    @BsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("scope")
    @BsonProperty("scope")
    private String scope;
    @JsonProperty("token_type")
    @BsonProperty("token_type")
    private String tokenType;

    /**
     * Token used in Authorization header
     */
    @JsonProperty("access_token")
    @BsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("access_token")
    @BsonProperty("access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("created_at")
    @BsonProperty("created_at")
    public Long getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    @BsonProperty("created_at")
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @JsonInclude
    @BsonIgnore
    public Date getCreatedAtAsDate() {
        return new Date(TimeUnit.SECONDS.toMillis(createdAt));
    }

    @JsonIgnore
    @BsonIgnore
    public LocalDate getExpiredAtAsLocalDate() {
        return getExpiresAt().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    /**
     * Seconds until the access_token expires (30 days default)
     */
    @JsonProperty("expires_in")
    @BsonProperty("expires_in")
    public Long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("expires_in")
    @BsonProperty("expires_in")
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @JsonIgnore
    @BsonIgnore
    public Date getExpiresAt() {
        return new Date(getCreatedAtAsDate().getTime() + TimeUnit.SECONDS.toMillis(expiresIn));
    }

    @JsonIgnore
    @BsonIgnore
    public long getDaysUntilExpired() {
        return LocalDate.now().until(getExpiredAtAsLocalDate(), ChronoUnit.DAYS);
    }

    @JsonIgnore
    @BsonIgnore
    public boolean isExpired() {
        return getDaysUntilExpired() < 0;
    }

    /**
     * Token used to get a new access_token
     */
    @JsonProperty("refresh_token")
    @BsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    @JsonProperty("refresh_token")
    @BsonProperty("refresh_token")
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @JsonProperty("scope")
    @BsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("scope")
    @BsonProperty("scope")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("token_type")
    @BsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("token_type")
    @BsonProperty("token_type")
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
