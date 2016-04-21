package com.koenv.universalminecraftapi.config;

public class WebServerSecureSection {
    private final boolean enabled;
    private final String keyStoreFile;
    private final String keystorePassword;
    private final String trustStoreFile;
    private final String trustStorePassword;

    private WebServerSecureSection(Builder builder) {
        this.enabled = builder.enabled;
        this.keyStoreFile = builder.keyStoreFile;
        this.keystorePassword = builder.keystorePassword;
        this.trustStoreFile = builder.trustStoreFile;
        this.trustStorePassword = builder.trustStorePassword;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }
    
    public static class Builder {
        private boolean enabled;
        private String keyStoreFile;
        private String keystorePassword;
        private String trustStoreFile;
        private String trustStorePassword;

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder keyStoreFile(String keyStoreFile) {
            this.keyStoreFile = keyStoreFile;
            return this;
        }

        public Builder keystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
            return this;
        }

        public Builder trustStoreFile(String trustStoreFile) {
            this.trustStoreFile = trustStoreFile;
            return this;
        }

        public Builder trustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public WebServerSecureSection build() {
            return new WebServerSecureSection(this);
        }
    }
}
