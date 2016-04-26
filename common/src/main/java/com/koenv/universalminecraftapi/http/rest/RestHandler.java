package com.koenv.universalminecraftapi.http.rest;

import com.koenv.universalminecraftapi.methods.OptionalParam;
import com.koenv.universalminecraftapi.methods.RethrowableException;
import com.koenv.universalminecraftapi.reflection.ParameterConverterManager;
import com.koenv.universalminecraftapi.util.Pair;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class RestHandler {
    private ParameterConverterManager parameterConverterManager;

    private List<RestResourceMethod> resources = new ArrayList<>();
    private Map<Class<?>, Map<String, RestOperationMethod>> operations = new HashMap<>();

    public RestHandler(ParameterConverterManager parameterConverterManager) {
        this.parameterConverterManager = parameterConverterManager;
    }

    public Object handle(@NotNull String path, @Nullable RestParameters parameters) throws RestException {
        List<RestResourceMethod> resources = findResources(path);

        if (resources.size() == 0) {
            throw new RestNotFoundException("No resources found that match " + path);
        }

        if (resources.size() > 1) {
            resources.sort((o1, o2) -> Integer.compare(RestUtils.splitPathByParts(o2.getPath()).size(), RestUtils.splitPathByParts(o1.getPath()).size())); // sort by number of path parts
            if (RestUtils.splitPathByParts(resources.get(0).getPath()).size() == RestUtils.splitPathByParts(resources.get(1).getPath()).size()) { // same specificity
                throw new RestNotFoundException("More than one resource for path " + path);
            }
        }

        RestResourceMethod restMethod = resources.get(0);

        List<String> resourceParts = RestUtils.splitPathByParts(restMethod.getPath());
        List<String> pathParts = RestUtils.splitPathByParts(path);

        int otherPathPartsSize = pathParts.size() - resourceParts.size();

        if (otherPathPartsSize == 0 && parameters != null && parameters.getMethod() != RestMethod.GET) {
            throw new RestMethodInvocationException("Unable to get resource " + path + " with method other than GET");
        }

        List<Pair<String, String>> paths = new ArrayList<>();

        for (int i = 0; i < resourceParts.size(); i++) {
            paths.add(Pair.of(resourceParts.get(i), pathParts.get(i)));
        }

        if (parameters != null && !parameters.hasPermission(restMethod)) {
            throw new RestForbiddenException("No permission to access resource " + restMethod.getPath());
        }

        Object result = invokeResource(parameters, restMethod, paths);

        if (result == null) {
            return null;
        }

        if (otherPathPartsSize > 0) {// we have at least one operation
            List<RestOperationMethod> operationMethods = getOperationMethods(pathParts, otherPathPartsSize, result.getClass());

            checkCorrectMethod(parameters, operationMethods);

            Object body = null;

            if (parameters != null) {
                body = parameters.getBody();
                if (body instanceof JSONObject) {
                    body = ((JSONObject) body).toMap();
                } else if (body instanceof JSONArray) {
                    body = ((JSONArray) body).toList();
                }
            }

            for (RestOperationMethod operationMethod : operationMethods) {
                if (parameters != null && !parameters.hasPermission(operationMethod)) {
                    throw new RestForbiddenException("No permission to access operation " + operationMethod.getPath());
                }

                result = invokeOperation(parameters, result, body, operationMethod);

                if (result == null) {
                    return null;
                }
            }
        }

        return result;
    }

    private void checkCorrectMethod(@Nullable RestParameters parameters, List<RestOperationMethod> operationMethods) throws RestMethodInvocationException {
        long postCount = operationMethods.stream().filter(restOperationMethod -> restOperationMethod.getRestMethod() == RestMethod.POST).count();

        if (postCount > 1) {
            throw new RestMethodInvocationException("Unable to invoke 2 methods with operation POST");
        }

        if (postCount == 1) {
            if (operationMethods.get(operationMethods.size() - 1).getRestMethod() != RestMethod.POST) {
                throw new RestMethodInvocationException("Cannot have an intermediary POST operation, must be the last operation");
            }
            if (parameters != null && parameters.getMethod() != RestMethod.POST) {
                throw new RestMethodInvocationException("The last operation is a POST operation, but did not receive POST method");
            }
        } else {
            if (parameters != null && parameters.getMethod() != RestMethod.GET) {
                throw new RestMethodInvocationException("The last operation is a GET operation, but did not receive GET method");
            }
        }
    }

    @NotNull
    private List<RestOperationMethod> getOperationMethods(List<String> pathParts, int i, Class<?> resultClass) throws RestNotFoundException {
        List<RestOperationMethod> operationMethods = new ArrayList<>();

        while (i > 0) {
            String operation = pathParts.get(pathParts.size() - i);

            RestOperationMethod operationMethod = findOperation(resultClass, operation);

            if (operationMethod == null) {
                throw new RestNotFoundException("Unable to find operation " + operation + " on object of type " + resultClass.getName());
            }

            operationMethods.add(operationMethod);

            resultClass = operationMethod.getJavaMethod().getReturnType();

            i--;
        }

        return operationMethods;
    }

    @Nullable
    private Object invokeResource(@Nullable RestParameters parameters, RestResourceMethod restMethod, List<Pair<String, String>> paths) throws RestMethodInvocationException {
        Method method = restMethod.getJavaMethod();

        List<Object> arguments = new ArrayList<>();

        Parameter[] javaParameters = method.getParameters();

        for (Parameter parameter : javaParameters) {
            String pathName = RestUtils.getParameterName(parameter);
            Optional<Pair<String, String>> value = paths.stream().filter(pair -> RestUtils.isParam(pair.getLeft())).filter(s -> Objects.equals(s.getLeft().substring(1), pathName)).findFirst();
            if (value.isPresent()) {
                String realValue = value.get().getRight();
                arguments.add(getPathParameter(restMethod, parameter, pathName, realValue));
                continue;
            }

            if (parameters != null) {
                if (parameter.getAnnotation(RestQuery.class) != null) {
                    arguments.add(getQueryParameter(parameters, restMethod, parameter));
                    continue;
                } else if (parameter.getAnnotation(RestPath.class) == null) {
                    Object invokerObject = parameters.get(parameter.getType());
                    if (invokerObject != null) {
                        arguments.add(invokerObject);
                        continue;
                    }
                }
            }

            throw new RestMethodInvocationException("Missing parameter " + parameter.getName() + " for REST resource " + restMethod.getPath());
        }

        if (arguments.size() != method.getParameterCount()) {
            throw new RestMethodInvocationException("Resource " + restMethod.getPath() + " requires " + method.getParameterCount() + " parameters, only got " + arguments.size());
        }

        Object result;
        try {
            result = restMethod.getJavaMethod().invoke(null, arguments.toArray());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException && e.getCause() instanceof RethrowableException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RestMethodInvocationException("Unable to invoke resource: " + restMethod.getPath() + ": " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
        } catch (Exception e) {
            throw new RestMethodInvocationException("Unable to invoke method " + restMethod.getPath() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
        }
        return result;
    }

    @NotNull
    private Object getPathParameter(RestResourceMethod restMethod, Parameter parameter, String pathName, String value) throws RestMethodInvocationException {
        boolean allowed = parameterConverterManager.checkParameter(value, parameter);
        if (allowed) {
            return value;
        } else {
            Object convertedParameter;
            try {
                convertedParameter = parameterConverterManager.convertParameterUntilFound(value, parameter);
            } catch (Exception e) {
                throw new RestMethodInvocationException(
                        "Error while converting parameter " + pathName + " for REST resource " + restMethod.getPath() +
                                " from " + value.getClass().getSimpleName() + " to " + parameter.getType().getSimpleName()
                                + ". Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                        e
                );
            }
            if (convertedParameter == null) {
                throw new RestMethodInvocationException("Wrong type for parameter " + pathName + " for REST resource " + restMethod.getPath());
            }
            return convertedParameter;
        }
    }

    @Nullable
    private Object getQueryParameter(@NotNull RestParameters parameters, RestResourceMethod restMethod, Parameter parameter) throws RestMethodInvocationException {
        Object queryParameter = null;
        RestQueryParamsMap map = parameters.getQueryParams();
        if (map != null) {
            String queryParamName = parameter.getAnnotation(RestQuery.class).value();
            map = map.get(queryParamName);

            if (map != null) {
                if (map.hasValue()) {
                    queryParameter = map.value();
                } else if (map.hasValues()) {
                    queryParameter = map.values();
                } else if (map.hasChildren()) {
                    queryParameter = convertQueryParamsMap(map);
                }
            }
        }

        if (queryParameter == null) {
            return null; // query parameters are always optional
        }

        boolean allowed = parameterConverterManager.checkParameter(queryParameter, parameter);
        if (allowed) {
            return queryParameter;
        }

        Object convertedParameter;
        try {
            convertedParameter = parameterConverterManager.convertParameterUntilFound(queryParameter, parameter);
        } catch (Exception e) {
            throw new RestMethodInvocationException(
                    "Error while converting query parameter " + queryParameter + " for REST resource " + restMethod.getPath() +
                            " from " + queryParameter.getClass().getSimpleName() + " to " + parameter.getType().getSimpleName()
                            + ". Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                    e
            );
        }
        if (convertedParameter == null) {
            throw new RestMethodInvocationException("Wrong type for parameter " + parameter.getName() + " for REST resource " + restMethod.getPath());
        }
        return convertedParameter;
    }

    @Nullable
    private Object invokeOperation(@Nullable RestParameters parameters, Object lastResult, Object body, RestOperationMethod operationMethod) throws RestMethodInvocationException {
        Method method = operationMethod.getJavaMethod();

        List<Object> operationArguments = new ArrayList<>();
        operationArguments.add(lastResult);

        if (method.getParameterCount() > 1) {
            Parameter[] javaOperationParameters = operationMethod.getJavaMethod().getParameters();

            for (int j = 1; j < operationMethod.getJavaMethod().getParameterCount(); j++) {
                Parameter javaParameter = javaOperationParameters[j];

                if (javaParameter.getAnnotation(RestBody.class) != null) {
                    operationArguments.add(getBodyParameter(body, operationMethod, javaParameter));
                    continue;
                }

                if (parameters != null) {
                    Object invokerObject = parameters.get(javaParameter.getType());
                    if (invokerObject != null) {
                        operationArguments.add(invokerObject);
                        continue;
                    }
                }

                throw new RestMethodInvocationException("Missing parameter '" + javaParameter.getName() + "' for REST operation '" + operationMethod.getPath() + "'");
            }
        }

        try {
            lastResult = operationMethod.getJavaMethod().invoke(null, operationArguments.toArray());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException && e.getCause() instanceof RethrowableException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RestMethodInvocationException("Unable to invoke operation: " + operationMethod.getPath() + ": " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
        } catch (Exception e) {
            throw new RestMethodInvocationException("Unable to invoke method " + operationMethod.getPath() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
        }
        return lastResult;
    }

    @Nullable
    private Object getBodyParameter(Object body, RestOperationMethod operationMethod, Parameter javaParameter) throws RestMethodInvocationException {
        String nameInBody = javaParameter.getAnnotation(RestBody.class).value();

        Object bodyParameter = null;

        if (!nameInBody.isEmpty()) {
            if (body != null && body instanceof Map) {
                bodyParameter = ((Map) body).get(nameInBody);
            }
        } else {
            bodyParameter = body;
        }

        if (bodyParameter == null) {
            if (javaParameter.getAnnotation(OptionalParam.class) == null) {
                if (!nameInBody.isEmpty()) {
                    throw new RestMethodInvocationException("Missing parameter in body: " + nameInBody + " for REST operation " + operationMethod.getPath());
                } else {
                    throw new RestMethodInvocationException("Missing body for REST operation " + operationMethod.getPath());
                }
            }
            return null;
        }

        boolean allowed = parameterConverterManager.checkParameter(bodyParameter, javaParameter);
        if (allowed) {
            return bodyParameter;
        }

        Object convertedParameter;
        try {
            convertedParameter = parameterConverterManager.convertParameterUntilFound(bodyParameter, javaParameter);
        } catch (Exception e) {
            if (!nameInBody.isEmpty()) {
                throw new RestMethodInvocationException(
                        "Error while converting parameter " + nameInBody + " for REST operation " + operationMethod.getPath() +
                                " from " + bodyParameter.getClass().getSimpleName() + " to " + javaParameter.getType().getSimpleName()
                                + ". Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                        e
                );
            } else {
                throw new RestMethodInvocationException(
                        "Error while converting body for REST operation " + operationMethod.getPath() +
                                " from " + bodyParameter.getClass().getSimpleName() + " to " + javaParameter.getType().getSimpleName()
                                + ". Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                        e
                );
            }
        }
        if (convertedParameter == null) {
            throw new RestMethodInvocationException("Wrong type for body for REST operation " + operationMethod.getPath());
        }
        return convertedParameter;
    }

    public List<RestResourceMethod> findResources(String path) {
        return resources.stream().filter(resource -> resource.matches(path)).collect(Collectors.toList());
    }

    public void registerClass(Class<?> clazz) throws RestMethodRegistrationException {
        for (java.lang.reflect.Method objectMethod : clazz.getMethods()) {
            if (objectMethod.getAnnotation(RestResource.class) != null) {
                resources.add(getRestResourceMethod(objectMethod));
            }

            if (objectMethod.getAnnotation(RestOperation.class) != null) {
                RestOperationMethod method = getRestOperationMethod(objectMethod);
                if (!operations.containsKey(method.getOperatesOn())) {
                    operations.put(method.getOperatesOn(), new HashMap<>());
                }
                operations.get(method.getOperatesOn()).put(method.getPath(), method);
            }
        }
    }

    private static RestResourceMethod getRestResourceMethod(Method method) {
        RestResource resourceAnnotation = method.getAnnotation(RestResource.class);

        if (!Modifier.isStatic(method.getModifiers())) {
            throw new RestMethodRegistrationException("REST resource methods must be static", method);
        }

        String path = resourceAnnotation.value();

        List<String> parts = RestUtils.splitPathByParts(path);
        List<String> parameterNames = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(RestPath.class) != null) {
                String pathName = parameter.getAnnotation(RestPath.class).value();
                parameterNames.add(pathName);
                if (!parts.contains(":" + pathName)) {
                    throw new RestMethodRegistrationException("Failed to find path parameter " + pathName, method);
                }
            }
            if (parameter.getAnnotation(RestBody.class) != null) {
                throw new RestMethodRegistrationException("A body can only be found on an operation", method);
            }
        }

        for (String part : parts) {
            if (RestUtils.isParam(part)) {
                if (!parameterNames.contains(part.substring(1))) {
                    throw new RestMethodRegistrationException("Failed to find method parameter " + part, method);
                }
            }
        }

        if (path.isEmpty()) {
            throw new RestMethodRegistrationException("REST resource paths must not be empty", method);
        }

        return new RestResourceMethod(path, method);
    }

    private static RestOperationMethod getRestOperationMethod(Method method) {
        RestOperation operationAnnotation = method.getAnnotation(RestOperation.class);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new RestMethodRegistrationException("REST operation methods must be static", method);
        }

        if (method.getParameterCount() < 1) {
            throw new RestMethodRegistrationException("REST operation methods must have at least 1 parameter: ", method);
        }

        if (!operationAnnotation.value().isAssignableFrom(method.getParameters()[0].getType())) {
            throw new RestMethodRegistrationException("REST operation methods must have the operated on class as first parameter", method);
        }

        String path = operationAnnotation.path();
        RestMethod restMethod = operationAnnotation.method();
        if (path.isEmpty()) {
            path = method.getName();
            if (path.startsWith("get")) {
                path = path.substring(3);
                path = path.substring(0, 1).toLowerCase() + path.substring(1);
                if (restMethod == RestMethod.DEFAULT) {
                    restMethod = RestMethod.GET;
                }
            } else if (path.startsWith("set")) {
                path = path.substring(3);
                path = path.substring(0, 1).toLowerCase() + path.substring(1);
                if (restMethod == RestMethod.DEFAULT) {
                    restMethod = RestMethod.POST;
                }
            }
        }

        boolean foundMultipleBodies = false;
        boolean foundSingleBody = false;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(RestBody.class) != null) {
                RestBody restBody = parameter.getAnnotation(RestBody.class);
                if (restBody.value().isEmpty()) {
                    if (foundSingleBody) {
                        throw new RestMethodRegistrationException("Only 1 empty @" + RestBody.class.getSimpleName() + " can be specified", method);
                    }
                    if (foundMultipleBodies) {
                        throw new RestMethodRegistrationException("No empty @" + RestBody.class.getSimpleName() + "s can be specified if there are @" + RestBody.class.getSimpleName() + "s with names", method);
                    }
                    foundSingleBody = true;
                } else {
                    if (foundSingleBody) {
                        throw new RestMethodRegistrationException("No empty @" + RestBody.class.getSimpleName() + "s can be specified if there are @" + RestBody.class.getSimpleName() + "s with names", method);
                    }
                    foundMultipleBodies = true;
                }
                if (restMethod != RestMethod.POST) {
                    if (restMethod == RestMethod.DEFAULT) {
                        restMethod = RestMethod.POST;
                    } else {
                        throw new RestMethodRegistrationException("When a @" + RestBody.class.getSimpleName() + " is specified, the method must be POST", method);
                    }
                }
            }
        }

        if (restMethod == RestMethod.DEFAULT) {
            restMethod = RestMethod.GET;
        }

        return new RestOperationMethod(operationAnnotation.value(), path, restMethod, method);
    }

    private static Map<String, Object> convertQueryParamsMap(RestQueryParamsMap map) {
        Map<String, Object> result = new HashMap<>();
        for (String key : map.getKeys()) {
            RestQueryParamsMap value = map.get(key);
            if (value.hasValue()) {
                result.put(key, value.value());
            } else if (value.hasValues()) {
                result.put(key, value.values());
            } else if (value.hasChildren()) {
                result.put(key, convertQueryParamsMap(value));
            }
        }
        return result;
    }

    private RestOperationMethod findOperation(Class<?> clazz, String operation) {
        Map<String, RestOperationMethod> operations = this.operations.get(clazz);
        RestOperationMethod method = null;
        if (operations != null) {
            method = operations.get(operation);
        }

        if (method == null) {
            if (clazz.getSuperclass() != null) {
                method = findOperation(clazz.getSuperclass(), operation);
                if (method != null) {
                    return method;
                }
            }
            for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                method = findOperation(interfaceClazz, operation);
                if (method != null) {
                    return method;
                }
            }
        } else {
            return method;
        }

        return null;
    }

    public List<RestResourceMethod> getResources() {
        return resources;
    }

    public Map<Class<?>, Map<String, RestOperationMethod>> getOperations() {
        return operations;
    }
}
