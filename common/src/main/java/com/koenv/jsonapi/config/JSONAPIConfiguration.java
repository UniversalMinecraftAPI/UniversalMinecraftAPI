package com.koenv.jsonapi.config;

import com.koenv.jsonapi.config.user.UsersConfiguration;

public class JSONAPIConfiguration {
    private final WebServerSection webServer;
    private final UsersConfiguration usersConfiguration;

    private JSONAPIConfiguration(Builder builder) {
        this.webServer = builder.webServer;
        this.usersConfiguration = builder.usersConfiguration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public WebServerSection getWebServer() {
        return webServer;
    }

    public UsersConfiguration getUsersConfiguration() {
        return usersConfiguration;
    }

    public static class Builder {
        private WebServerSection webServer;
        private UsersConfiguration usersConfiguration;

        private Builder() {
        }

        public Builder webServer(WebServerSection webServer) {
            this.webServer = webServer;
            return this;
        }

        public Builder usersConfiguration(UsersConfiguration usersConfiguration) {
            this.usersConfiguration = usersConfiguration;
            return this;
        }

        public JSONAPIConfiguration build() {
            return new JSONAPIConfiguration(this);
        }
    }
}
