package com.koenv.universalminecraftapi.config.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UsersConfiguration {
    private List<UserSection> users;
    private List<GroupSection> groups;

    private UsersConfiguration(Builder builder) {
        this.users = builder.users;
        this.groups = builder.groups;
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

    public static class Builder {
        private List<UserSection> users = Collections.emptyList();
        private List<GroupSection> groups = Collections.emptyList();

        private Builder() {
        }

        public Builder users(List<UserSection> users) {
            this.users = users;
            return this;
        }

        public Builder users(UserSection... users) {
            return users(Arrays.asList(users));
        }

        public Builder groups(List<GroupSection> groups) {
            this.groups = groups;
            return this;
        }

        public Builder groups(GroupSection... groups) {
            return groups(Arrays.asList(groups));
        }

        public UsersConfiguration build() {
            return new UsersConfiguration(this);
        }
    }
}
