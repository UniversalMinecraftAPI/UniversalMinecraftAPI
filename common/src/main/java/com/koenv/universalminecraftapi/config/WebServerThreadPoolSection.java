package com.koenv.universalminecraftapi.config;

public class WebServerThreadPoolSection {
    private int maxThreads;
    private int minThreads;
    private int idleTimeoutMillis;

    private WebServerThreadPoolSection(Builder builder) {
        this.maxThreads = builder.maxThreads;
        this.minThreads = builder.minThreads;
        this.idleTimeoutMillis = builder.idleTimeoutMillis;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTimeoutMillis() {
        return idleTimeoutMillis;
    }
    
    public static class Builder {
        private int maxThreads;
        private int minThreads;
        private int idleTimeoutMillis;

        private Builder() {
        }

        public Builder maxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
            return this;
        }

        public Builder minThreads(int minThreads) {
            this.minThreads = minThreads;
            return this;
        }

        public Builder idleTimeoutMillis(int idleTimeoutMillis) {
            this.idleTimeoutMillis = idleTimeoutMillis;
            return this;
        }

        public WebServerThreadPoolSection build() {
            return new WebServerThreadPoolSection(this);
        }
    }
}
