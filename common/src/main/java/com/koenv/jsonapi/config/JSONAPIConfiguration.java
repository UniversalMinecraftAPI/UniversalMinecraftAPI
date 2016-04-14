package com.koenv.jsonapi.config;

public class JSONAPIConfiguration {
    private final WebServerSection webServer;

    private JSONAPIConfiguration(Builder builder) {
        this.webServer = builder.webServer;
    }

    public WebServerSection getWebServer() {
        return webServer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WebServerSection webServer;

        private Builder() {
        }

        public Builder webServer(WebServerSection webServer) {
            this.webServer = webServer;
            return this;
        }

        public JSONAPIConfiguration build() {
            return new JSONAPIConfiguration(this);
        }
    }
}
