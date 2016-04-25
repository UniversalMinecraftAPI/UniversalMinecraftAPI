package com.koenv.universalminecraftapi.http;

import com.koenv.universalminecraftapi.http.rest.*;
import com.koenv.universalminecraftapi.users.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebServerRestParameters implements RestParameters {
    private User user;
    private WebServerQueryParamsMap queryParamsMap;
    private Object body;
    private RestMethod method;

    public WebServerRestParameters(User user, WebServerQueryParamsMap queryParamsMap, Object body, RestMethod method) {
        this.user = user;
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
