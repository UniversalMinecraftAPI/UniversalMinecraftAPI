package com.koenv.jsonapi.users.model;

import com.koenv.jsonapi.users.voters.VoterResponse;

import java.util.List;
import java.util.stream.Stream;

public class Group {
    private String name;
    private List<Permission> permissions;

    private Group(Builder builder) {
        this.name = builder.name;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Stream<VoterResponse> canAccessNamespaceMethod(String namespace, String method) {
        return permissions.stream().flatMap(permission -> permission.canAccessNamespaceMethod(namespace, method));
    }

    public Stream<VoterResponse> canAccessClassMethod(String clazz, String method) {
        return permissions.stream().flatMap(permission -> permission.canAccessClassMethod(clazz, method));
    }

    public Stream<VoterResponse> canAccessStream(String stream) {
        return permissions.stream().map(permission -> permission.canAccessStream(stream));
    }

    public static class Builder {
        private String name;
        private List<Permission> permissions;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
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
