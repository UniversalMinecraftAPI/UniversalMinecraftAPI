package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.methods.exception.MethodInvocationException;
import com.koenv.jsonapi.parser.expressions.Expression;
import com.koenv.jsonapi.parser.expressions.MethodCallExpression;
import com.koenv.jsonapi.parser.expressions.NamespaceExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInvoker {
    private static final String DEFAULT_NAMESPACE = "this";

    private Map<String, Map<String, Method>> namespaceMethodsMap;

    public MethodInvoker() {
        this.namespaceMethodsMap = new HashMap<>();
    }

    public void invokeMethod(List<Expression> expressions) throws MethodInvocationException {
        String namespace = null;
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            if (expression instanceof NamespaceExpression) {
                if (i != 0) {
                    throw new MethodInvocationException("Namespace can only be in first position");
                }
                namespace = ((NamespaceExpression) expression).getName();
            } else if (expression instanceof MethodCallExpression) {
                if (i == 0 || (i == 1 && namespace != null)) {
                    invokeMethod(namespace, (MethodCallExpression) expression);
                }
            }
        }
    }

    private void invokeMethod(String namespace, MethodCallExpression methodCallExpression) throws MethodInvocationException {
        Map<String, Method> methods = getNamespace(namespace);
        if (methods == null) {
            throw new MethodInvocationException("Unable to find namespace " + getNamespaceName(namespace));
        }
        Method method = getMethod(methods, methodCallExpression.getMethodName());
        if (method == null) {
            throw new MethodInvocationException("Unable to find method " + methodCallExpression.getMethodName() + " in namespace " + getNamespaceName(namespace));
        }
    }

    public void registerMethods(Object object) {
        for (java.lang.reflect.Method objectMethod : object.getClass().getMethods()) {
            APIMethod annotation = objectMethod.getAnnotation(APIMethod.class);
            if (annotation == null) {
                continue;
            }
            String namespace = getNamespaceName(annotation.namespace());
            Method method = new Method(namespace, objectMethod.getName());
            registerMethod(method);
        }
    }

    private void registerMethod(Method method) {
        if (namespaceMethodsMap.get(method.getNamespace()) == null) {
            namespaceMethodsMap.put(method.getNamespace(), new HashMap<>());
        }
        namespaceMethodsMap.get(method.getNamespace()).put(method.getName(), method);
    }

    private String getNamespaceName(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return DEFAULT_NAMESPACE;
        }
        return namespace;
    }

    private Map<String, Method> getNamespace(String namespace) {
        return namespaceMethodsMap.get(getNamespaceName(namespace));
    }

    private Method getMethod(Map<String, Method> methods, String methodName) {
        return methods.get(methodName);
    }
}
