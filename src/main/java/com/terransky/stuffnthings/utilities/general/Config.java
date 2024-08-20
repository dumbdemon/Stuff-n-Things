package com.terransky.stuffnthings.utilities.general;

import com.terransky.stuffnthings.utilities.general.configobjects.CoreConfig;
import com.terransky.stuffnthings.utilities.general.configobjects.DatabaseConfig;
import com.terransky.stuffnthings.utilities.general.configobjects.TokensConfig;

@SuppressWarnings("unused")
public class Config {
    private CoreConfig core;
    private DatabaseConfig databaseConfig;
    private TokensConfig tokens;

    Config() {
    }

    public CoreConfig getCore() {
        return core;
    }

    public void setCore(CoreConfig core) {
        this.core = core;
    }

    public DatabaseConfig getDatabase() {
        return databaseConfig;
    }

    public void setDatabase(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public TokensConfig getTokens() {
        return tokens;
    }

    public void setTokens(TokensConfig tokens) {
        this.tokens = tokens;
    }
}
