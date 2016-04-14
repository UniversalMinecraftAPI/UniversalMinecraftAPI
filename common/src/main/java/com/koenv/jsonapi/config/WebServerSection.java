package com.koenv.jsonapi.config;

public class WebServerSection {
    private final String ipAddress;
    private final int port;
    private final WebServerSecureSection secure;
    private final WebServerThreadPoolSection threadPool;

    private WebServerSection(Builder builder) {
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.secure = builder.secure;
        this.threadPool = builder.threadPool;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public WebServerSecureSection getSecure() {
        return secure;
    }

    public WebServerThreadPoolSection getThreadPool() {
        return threadPool;
    }

    public static class Builder {
        private String ipAddress;
        private int port;
        private WebServerSecureSection secure;
        private WebServerThreadPoolSection threadPool;

        private Builder() {
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder secure(WebServerSecureSection secure) {
            this.secure = secure;
            return this;
        }

        public Builder threadPool(WebServerThreadPoolSection threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public WebServerSection build() {
            return new WebServerSection(this);
        }
    }
}
