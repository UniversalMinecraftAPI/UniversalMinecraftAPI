package com.koenv.universalminecraftapi.users.model;

import com.koenv.universalminecraftapi.permissions.PermissionTree;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String passwordType;
    private List<Group> groups;
    private PermissionTree permissions;

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.passwordType = builder.passwordType;
        this.groups = builder.groups;
        this.permissions = builder.permissions;
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

    public PermissionTree getPermissions() {
        return permissions;
    }

    public int getPermission(String path) {
        return permissions.get(path);
    }

    public int getPermission(String[] parts) {
        return permissions.get(parts);
    }

    public boolean hasPermission(String path) {
        return getPermission(path) > 0;
    }

    public boolean hasPermission(String[] parts) {
        return getPermission(parts) > 0;
    }

    public static class Builder {
        private String username;
        private String password;
        private String passwordType;
        private List<Group> groups;
        private PermissionTree permissions;

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

        public Builder permissions(PermissionTree permissions) {
            this.permissions = permissions;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
