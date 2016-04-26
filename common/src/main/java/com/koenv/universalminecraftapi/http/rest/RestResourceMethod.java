package com.koenv.universalminecraftapi.http.rest;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class RestResourceMethod {
    private String path;
    private Method method;

    public RestResourceMethod(String path, Method method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public boolean matches(String path) {
        if ((this.path.endsWith("/") && !path.endsWith("/")) || (!this.path.endsWith("/") && path.endsWith("/"))) {
            return false; // mismatch between ending with slash
        }

        if (Objects.equals(this.path, path)) {
            return true; // exact match
        }

        List<String> ownPathParts = RestUtils.splitPathByParts(this.path);
        List<String> givenPathParts = RestUtils.splitPathByParts(path);

        if (givenPathParts.size() < ownPathParts.size()) {
            return false; // can never be a match
        }

        for (int i = 0; i < ownPathParts.size(); i++) {
            String ownPathPart = ownPathParts.get(i);
            String givenPathPart = givenPathParts.get(i);

            if (Objects.equals(ownPathPart, givenPathPart)) {
                continue;
            }

            if (RestUtils.isParam(ownPathPart)) {
                continue;
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestResourceMethod that = (RestResourceMethod) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return method != null ? method.equals(that.method) : that.method == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RestResourceMethod{" +
                "path='" + path + '\'' +
                ", method=" + method +
                '}';
    }
}
