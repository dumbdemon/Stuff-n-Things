package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.interfaces.MapperObject;
import org.jetbrains.annotations.NotNull;

/**
 * Root POJO for OAUTH requests to Kitsu.io
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class KitsuAuthRequest implements MapperObject {

    @JsonProperty("grant_type")
    private String grantType;

    /**
     * Base constructor to create a new request
     *
     * @param grantType The type of request
     */
    protected KitsuAuthRequest(@NotNull GrantType grantType) {
        this.grantType = grantType.getType();
    }

    /**
     * Get the grant type
     *
     * @return String containing the grant type
     */
    @JsonProperty("grant_type")
    public String getGrantType() {
        return grantType;
    }

    /**
     * Set the grant type
     *
     * @param grantType The grant type preferably from {@link GrantType}
     */
    @JsonProperty("grant_type")
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**
     * The types of grants for Kitsu.io
     */
    public enum GrantType {
        REFRESH("refresh_token"),
        PASSWORD("password"),
        AUTHORIZATION_CODE() {
            @Override
            public String getType() {
                throw new IllegalArgumentException("This type of grant is not yet been implemented");
            }
        },
        /**
         * Due to the use case, this is preferred, but it has yet to be implemented
         */
        CLIENT_CREDENTIALS() {
            @Override
            public String getType() {
                throw new IllegalArgumentException("This type of grant is not yet been implemented");
            }
        };

        private final String type;

        GrantType() {
            this(null);
        }

        GrantType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
