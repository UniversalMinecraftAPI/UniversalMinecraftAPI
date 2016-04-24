package com.koenv.universalminecraftapi.http.rest;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RestUtils {
    public static List<String> splitPathByParts(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }

    public static boolean isParam(String part) {
        return part.startsWith(":");
    }

    public static String getParameterName(Parameter parameter) {
        if (parameter.getAnnotation(RestPath.class) != null) {
            return parameter.getAnnotation(RestPath.class).value();
        }

        return parameter.getName();
    }
}
