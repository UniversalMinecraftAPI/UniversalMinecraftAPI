package com.koenv.jsonapi.users.model;

import com.koenv.jsonapi.users.voters.VoterUtils;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String passwordType;
    private List<Group> groups;

    private User(Builder builder) {
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

    public String getPasswordType() {
        return passwordType;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public boolean canAccessNamespaceMethod(String namespace, String method) {
        return VoterUtils.isUnanimous(groups.stream().flatMap(group -> group.canAccessNamespaceMethod(namespace, method)));
    }

    public boolean canAccessClassMethod(String clazz, String method) {
        return VoterUtils.isUnanimous(groups.stream().flatMap(group -> group.canAccessClassMethod(clazz, method)));
    }

    public boolean canAccessStream(String stream) {
        return VoterUtils.isUnanimous(groups.stream().flatMap(group -> group.canAccessStream(stream)));
    }

    public static class Builder {
        private String username;
        private String password;
        private String passwordType;
        private List<Group> groups;

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

        public Builder passwordType(String passwordType) {
            this.passwordType = passwordType;
            return this;
        }

        public Builder groups(List<Group> groups) {
            this.groups = groups;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
