package com.koenv.universalminecraftapi.config.user;

public class PermissionSection {
    private String name;
    private int value;

    private PermissionSection(Builder builder) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionSection that = (PermissionSection) o;

        if (value != that.value) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "PermissionSection{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
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

        public PermissionSection build() {
            return new PermissionSection(this);
        }
    }
}
