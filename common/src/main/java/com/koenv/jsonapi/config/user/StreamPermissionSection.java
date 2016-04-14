package com.koenv.jsonapi.config.user;

import java.util.List;

public class StreamPermissionSection {
    private PermissionSection.Type type;
    private List<String> streams;

    private StreamPermissionSection(Builder builder) {
        this.type = builder.type;
        this.streams = builder.streams;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PermissionSection.Type getType() {
        return type;
    }

    public List<String> getStreams() {
        return streams;
    }

    public static class Builder {
        private PermissionSection.Type type;
        private List<String> streams;

        private Builder() {
        }

        public Builder type(PermissionSection.Type type) {
            this.type = type;
            return this;
        }

        public Builder streams(List<String> streams) {
            this.streams = streams;
            return this;
        }

        public StreamPermissionSection build() {
            return new StreamPermissionSection(this);
        }
    }
}
