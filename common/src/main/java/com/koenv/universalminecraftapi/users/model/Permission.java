package com.koenv.universalminecraftapi.users.model;

public class Permission {
    private final String name;
    private final int value;

    private Permission(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }


    public static class Builder {
        private String name;
        private int value;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Permission build() {
            return new Permission(this);
        }
    }
}
