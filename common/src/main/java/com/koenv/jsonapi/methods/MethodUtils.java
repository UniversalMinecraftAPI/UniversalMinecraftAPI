package com.koenv.jsonapi.methods;

import java.lang.reflect.Parameter;

public class MethodUtils {
    public static boolean shouldExcludeFromDoc(Parameter parameter) {
        return parameter.getAnnotation(ExcludeFromDoc.class) != null || parameter.getType().getAnnotation(ExcludeFromDoc.class) != null;
    }
}
