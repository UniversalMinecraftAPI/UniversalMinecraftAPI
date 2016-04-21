package com.koenv.universalminecraftapi.config.user;

import java.util.List;

public class UsersConfiguration {
    private List<UserSection> users;
    private List<GroupSection> groups;
    private List<PermissionSection> permissions;

    private UsersConfiguration(Builder builder) {
        this.users = builder.users;
        this.groups = builder.groups;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<UserSection> getUsers() {
        return users;
    }

    public List<GroupSection> getGroups() {
        return groups;
    }

    public List<PermissionSection> getPermissions() {
        return permissions;
    }

    public static class Builder {
        private List<UserSection> users;
        private List<GroupSection> groups;
        private List<PermissionSection> permissions;

        private Builder() {
        }

        public Builder users(List<UserSection> users) {
            this.users = users;
            return this;
        }

        public Builder groups(List<GroupSection> groups) {
            this.groups = groups;
            return this;
        }

        public Builder permissions(List<PermissionSection> permissions) {
            this.permissions = permissions;
            return this;
        }

        public UsersConfiguration build() {
            return new UsersConfiguration(this);
        }
    }
}
