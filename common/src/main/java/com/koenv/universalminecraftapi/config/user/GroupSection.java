package com.koenv.universalminecraftapi.config.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupSection {
    private String name;
    private int defaultPermission;
    private List<String> inheritsFrom;
    private List<PermissionSection> permissions;

    public GroupSection(Builder builder) {
        this.name = builder.name;
        this.defaultPermission = builder.defaultPermission;
        this.inheritsFrom = builder.inheritsFrom;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public int getDefaultPermission() {
        return defaultPermission;
    }

    public List<String> getInheritsFrom() {
        return inheritsFrom;
    }

    public List<PermissionSection> getPermissions() {
        return permissions;
    }

    public static class Builder {
        private String name;
        private int defaultPermission = 0;
        private List<String> inheritsFrom = Collections.emptyList();
        private List<PermissionSection> permissions = Collections.emptyList();

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder defaultPermission(int defaultPermission) {
            this.defaultPermission = defaultPermission;
            return this;
        }

        public Builder inheritsFrom(List<String> inheritsFrom) {
            this.inheritsFrom = inheritsFrom;
            return this;
        }

        public Builder inheritsFrom(String... inheritsFrom) {
            return inheritsFrom(Arrays.asList(inheritsFrom));
        }

        public Builder permissions(List<PermissionSection> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder permissions(PermissionSection... permissions) {
            return permissions(Arrays.asList(permissions));
        }

        public GroupSection build() {
            return new GroupSection(this);
        }
    }
}
