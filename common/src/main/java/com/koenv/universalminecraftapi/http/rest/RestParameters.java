package com.koenv.universalminecraftapi.http.rest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RestParameters {
    boolean hasPermission(IRestMethod method);

    /**
     * Gets an object to use in a method call, should not be an expensive call. This is mostly used for global objects
     * that are otherwise not available to the method and can via this method be supplied via this method.
     *
     * @param clazz The class of which to get an instance to
     * @return The object of this class or null if this class is not available
     */
    @Nullable Object get(Class<?> clazz);

    @Nullable RestQueryParamsMap getQueryParams();

    @Nullable Object getBody();

    @NotNull RestMethod getMethod();
}
