package com.koenv.universalminecraftapi.config;

public class UniversalMinecraftAPIRootConfiguration {
    private final WebServerSection webServer;

    private UniversalMinecraftAPIRootConfiguration(Builder builder) {
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

        public UniversalMinecraftAPIRootConfiguration build() {
            return new UniversalMinecraftAPIRootConfiguration(this);
        }
    }
}
