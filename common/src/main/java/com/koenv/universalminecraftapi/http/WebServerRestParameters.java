package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.http.rest.*;
import com.koenv.universalminecraftapi.methods.Invoker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WebServerRestParameters implements RestParameters {
    private Invoker invoker;
    private WebServerQueryParamsMap queryParamsMap;
    private Object body;
    private RestMethod method;

    public WebServerRestParameters(Invoker invoker, WebServerQueryParamsMap queryParamsMap, Object body, RestMethod method) {
        this.invoker = invoker;
        this.queryParamsMap = queryParamsMap;
        this.body = body;
        this.method = method;
    }

    @Override
    public boolean hasPermission(RestResourceMethod method) {
        return true;
    }

    @Override
    public boolean hasPermission(RestOperationMethod method) {
        return true;
    }

    @Override
    public @Nullable Object get(Class<?> clazz) {
        if (Objects.equals(clazz, Invoker.class)) {
            return invoker;
        }
        return null;
    }

    @Override
    public @Nullable RestQueryParamsMap getQueryParams() {
        return queryParamsMap;
    }

    @Override
    public @Nullable Object getBody() {
        return body;
    }

    @Override
    public @NotNull RestMethod getMethod() {
        return method;
    }
}
