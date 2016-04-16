package com.koenv.jsonapi.users.model;

import com.koenv.jsonapi.users.voters.VoterResponse;
import com.koenv.jsonapi.users.voters.VoterUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Permission {
    private final String name;
    private final List<NamespacePermissions> namespaces;
    private final List<ClassPermissions> classes;
    private final StreamPermissions streams;

    private Permission(Builder builder) {
        this.name = builder.name;
        this.namespaces = builder.namespaces;
        this.classes = builder.classes;
        this.streams = builder.streams;
    }

    protected Permission(String name, List<NamespacePermissions> namespaces, List<ClassPermissions> classes, StreamPermissions streams) {
        this.name = name;
        this.namespaces = namespaces;
        this.classes = classes;
        this.streams = streams;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Stream<VoterResponse> canAccessNamespaceMethod(String namespace, String method) {
        return canAccessBase(namespaces, namespace, method);
    }

    public Stream<VoterResponse> canAccessClassMethod(String clazz, String method) {
        return canAccessBase(classes, clazz, method);
    }

    public VoterResponse canAccessStream(String stream) {
        return streams.canAccess(stream);
    }

    private Stream<VoterResponse> canAccessBase(List<? extends BasePermissions> perms, String name, String method) {
        return perms.stream().map(perm -> perm.canAccess(name, method));
    }

    public abstract static class BasePermissions {
        private final String name;
        private final PermissionType type;
        private final List<String> methods;

        public BasePermissions(String name, PermissionType type, List<String> methods) {
            this.name = name;
            this.type = type;
            this.methods = methods;
        }

        public String getName() {
            return name;
        }

        public PermissionType getType() {
            return type;
        }

        public List<String> getMethods() {
            return methods;
        }

        public VoterResponse canAccess(String name, String method) {
            if (!Objects.equals(this.name, name)) {
                return VoterResponse.NEUTRAL;
            }
            return VoterUtils.canAccess(type, methods, method);
        }
    }

    public static class NamespacePermissions extends BasePermissions {
        private NamespacePermissions(Builder builder) {
            super(builder.name, builder.type, builder.methods);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String name;
            private PermissionType type;
            private List<String> methods;

            private Builder() {

            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder type(PermissionType type) {
                this.type = type;
                return this;
            }

            public Builder methods(List<String> methods) {
                this.methods = methods;
                return this;
            }

            public NamespacePermissions build() {
                return new NamespacePermissions(this);
            }
        }
    }

    public static class ClassPermissions extends BasePermissions {
        private ClassPermissions(Builder builder) {
            super(builder.name, builder.type, builder.methods);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String name;
            private PermissionType type;
            private List<String> methods;

            private Builder() {

            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder type(PermissionType type) {
                this.type = type;
                return this;
            }

            public Builder methods(List<String> methods) {
                this.methods = methods;
                return this;
            }

            public ClassPermissions build() {
                return new ClassPermissions(this);
            }
        }
    }

    public static class StreamPermissions {
        private final PermissionType type;
        private final List<String> streams;

        private StreamPermissions(Builder builder) {
            this.type = builder.type;
            this.streams = builder.streams;
        }

        public static Builder builder() {
            return new Builder();
        }

        public PermissionType getType() {
            return type;
        }

        public List<String> getStreams() {
            return streams;
        }

        public VoterResponse canAccess(String stream) {
            return VoterUtils.canAccess(type, streams, stream);
        }

        public static class Builder {
            private PermissionType type;
            private List<String> streams;

            public Builder type(PermissionType type) {
                this.type = type;
                return this;
            }

            public Builder streams(List<String> streams) {
                this.streams = streams;
                return this;
            }

            public StreamPermissions build() {
                return new StreamPermissions(this);
            }
        }
    }

    public static class Builder {
        private String name;
        private List<NamespacePermissions> namespaces;
        private List<ClassPermissions> classes;
        private StreamPermissions streams;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder namespaces(List<NamespacePermissions> namespaces) {
            this.namespaces = namespaces;
            return this;
        }

        public Builder classes(List<ClassPermissions> classes) {
            this.classes = classes;
            return this;
        }

        public Builder streams(StreamPermissions streams) {
            this.streams = streams;
            return this;
        }

        public Permission build() {
            return new Permission(this);
        }
    }
}
