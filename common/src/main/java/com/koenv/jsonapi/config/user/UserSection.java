package com.koenv.jsonapi.config.user;

import java.util.List;

public class UserSection {
    private String username;
    private String password;
    private PasswordType passwordType;
    private List<String> groups;

    private UserSection(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.passwordType = builder.passwordType;
        this.groups = builder.groups;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public PasswordType getPasswordType() {
        return passwordType;
    }

    public List<String> getGroups() {
        return groups;
    }

    public enum PasswordType {
        PLAIN,
        MD5,
        SHA1,
        SHA256,
        SHA512,
        BCRYPT
    }

    public static class Builder {
        private String username;
        private String password;
        private PasswordType passwordType;
        private List<String> groups;

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder passwordType(PasswordType passwordType) {
            this.passwordType = passwordType;
            return this;
        }

        public Builder groups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public UserSection build() {
            return new UserSection(this);
        }
    }
}
