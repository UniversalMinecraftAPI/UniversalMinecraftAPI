package com.koenv.jsonapi.methods;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.StringJoiner;

public class MethodUtils {
    public static boolean shouldExcludeFromDoc(Parameter parameter) {
        return parameter.getAnnotation(ExcludeFromDoc.class) != null || parameter.getType().getAnnotation(ExcludeFromDoc.class) != null;
    }

    /**
     * Gets a method declaration. This will be in the format: `methodName(methodParameters)`
     *
     * @param method Method for which to get the method declaration.
     * @return A string representation of the method.
     */
    public static String getMethodDeclaration(AbstractMethod method) {
        int skipParameters = 0;

        StringBuilder stringBuilder = new StringBuilder();
        if (method instanceof NamespacedMethod) {
            stringBuilder.append(((NamespacedMethod) method).getNamespace());
        } else if (method instanceof ClassMethod) {
            stringBuilder.append("<");
            stringBuilder.append(((ClassMethod) method).getOperatesOn().getSimpleName());
            stringBuilder.append(">");
            skipParameters = 1;
        }
        stringBuilder.append(".");
        stringBuilder.append(method.getName());
        stringBuilder.append("(");
        Parameter[] parameters = method.getJavaMethod().getParameters();
        StringJoiner joiner = new StringJoiner(", ");

        Arrays.asList(parameters)
                .stream()
                .skip(skipParameters)
                .filter(parameter -> !shouldExcludeFromDoc(parameter))
                .forEach(parameter -> joiner.add(parameter.getType().getSimpleName()));

        stringBuilder.append(joiner.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
