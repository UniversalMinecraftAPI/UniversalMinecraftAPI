package com.koenv.jsonapi.config.user;

import java.util.List;

public class GroupSection {
    private String name;
    private List<String> permissions;

    private GroupSection(Builder builder) {
        this.name = builder.name;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public static class Builder {
        private String name;
        private List<String> permissions;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public GroupSection build() {
            return new GroupSection(this);
        }
    }
}
