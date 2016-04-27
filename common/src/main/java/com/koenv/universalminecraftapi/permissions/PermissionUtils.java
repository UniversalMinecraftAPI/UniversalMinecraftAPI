package com.koenv.universalminecraftapi.permissions;

import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.http.rest.RestUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.StringJoiner;

public class PermissionUtils {
    /**
     * Gets the permission path for a Java method
     * <p>
     * First, it checks for the {@link RequiresPermission} annotation. If that is found, it uses that value.
     * <p>
     * If not, it checks for the {@link RestResource} annotation. If it is found, it converts paths it finds into
     * probable permissions.
     * <p>
     * For example:
     * <ul>
     * <li><code>uma/invoker</code> = <code>uma.invoker</code></li>
     * <li><code>uma/platform/version</code> = <code>uma.platform.version</code></li>
     * </ul>
     * <p>
     * But not:
     * <ul>
     * <li><code>players/:name</code>: It contains a parameter</li>
     * </ul>
     *
     * If neither are found, it returns an empty string to denote the default permission.
     *
     * @param method The method to find the permission for
     * @return The permission path
     */
    public static String getPermissionPath(Method method) {
        RequiresPermission permissionAnnotation = method.getAnnotation(RequiresPermission.class);
        if (permissionAnnotation != null) {
            return permissionAnnotation.value();
        }
        RestResource resourceAnnotation = method.getAnnotation(RestResource.class);
        if (resourceAnnotation != null) {
            List<String> parts = RestUtils.splitPathByParts(resourceAnnotation.value());
            if (!parts.stream().anyMatch(RestUtils::isParam)) {
                StringJoiner joiner = new StringJoiner(".");
                parts.forEach(joiner::add);
                return joiner.toString();
            }
        }
        return ""; // the default permission
    }
}
