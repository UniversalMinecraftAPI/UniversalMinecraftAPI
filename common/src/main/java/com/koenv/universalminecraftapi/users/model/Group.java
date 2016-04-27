package com.koenv.universalminecraftapi.users.model;

import java.util.List;

public class Group {
    private String name;
    private int defaultPermission;
    private List<String> inheritsFrom;
    private List<Permission> permissions;

    private Group(Builder builder) {
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

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (defaultPermission != group.defaultPermission) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        if (inheritsFrom != null ? !inheritsFrom.equals(group.inheritsFrom) : group.inheritsFrom != null) return false;
        return permissions != null ? permissions.equals(group.permissions) : group.permissions == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + defaultPermission;
        result = 31 * result + (inheritsFrom != null ? inheritsFrom.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", defaultPermission=" + defaultPermission +
                ", inheritsFrom=" + inheritsFrom +
                ", permissions=" + permissions +
                '}';
    }

    public static class Builder {
        private String name;
        private int defaultPermission;
        private List<String> inheritsFrom;
        private List<Permission> permissions;

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

        public Builder permissions(List<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }
}
