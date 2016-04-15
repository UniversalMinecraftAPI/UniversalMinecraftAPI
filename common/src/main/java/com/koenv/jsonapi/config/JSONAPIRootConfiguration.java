package com.koenv.jsonapi.config;

public class JSONAPIRootConfiguration {
    private final WebServerSection webServer;

    private JSONAPIRootConfiguration(Builder builder) {
        this.webServer = builder.webServer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public WebServerSection getWebServer() {
        return webServer;
    }

    public static class Builder {
        private WebServerSection webServer;

        private Builder() {
        }

        public Builder webServer(WebServerSection webServer) {
            this.webServer = webServer;
            return this;
        }

        public JSONAPIRootConfiguration build() {
            return new JSONAPIRootConfiguration(this);
        }
    }
}
