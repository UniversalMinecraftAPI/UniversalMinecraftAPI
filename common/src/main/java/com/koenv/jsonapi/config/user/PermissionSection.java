package com.koenv.jsonapi.config.user;

import java.util.List;

public class PermissionSection {
    private String name;
    private List<NamespacePermissionSection> namespaces;
    private List<ClassPermissionSection> classes;
    private StreamPermissionSection streams;

    private PermissionSection(Builder builder) {
        this.name = builder.name;
        this.namespaces = builder.namespaces;
        this.classes = builder.classes;
        this.streams = builder.streams;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public List<NamespacePermissionSection> getNamespaces() {
        return namespaces;
    }

    public List<ClassPermissionSection> getClasses() {
        return classes;
    }

    public StreamPermissionSection getStreams() {
        return streams;
    }

    public enum Type {
        WHITELIST,
        BLACKLIST;
    }

    public static class Builder {
        private String name;
        private List<NamespacePermissionSection> namespaces;
        private List<ClassPermissionSection> classes;
        private StreamPermissionSection streams;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder namespaces(List<NamespacePermissionSection> namespaces) {
            this.namespaces = namespaces;
            return this;
        }

        public Builder classes(List<ClassPermissionSection> classes) {
            this.classes = classes;
            return this;
        }

        public Builder streams(StreamPermissionSection streams) {
            this.streams = streams;
            return this;
        }

        public PermissionSection build() {
            return new PermissionSection(this);
        }
    }
}
