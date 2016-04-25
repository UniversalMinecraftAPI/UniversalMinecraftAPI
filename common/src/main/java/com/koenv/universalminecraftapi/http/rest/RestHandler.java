package com.koenv.universalminecraftapi.http.rest;

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
            // TODO: Check for more levels deep
            throw new RestNotFoundException("More than one resource for path " + path);
        }

        RestResourceMethod restMethod = resources.get(0);

        List<String> resourceParts = RestUtils.splitPathByParts(restMethod.getPath());
        List<String> pathParts = RestUtils.splitPathByParts(path);

        List<Pair<String, String>> paths = new ArrayList<>();

        for (int i = 0; i < resourceParts.size(); i++) {
            paths.add(Pair.of(resourceParts.get(i), pathParts.get(i)));
        }

        Method method = restMethod.getMethod();

        if (parameters != null && !parameters.hasPermission(restMethod)) {
            throw new RestForbiddenException("No permission to access resource " + restMethod.getPath());
        }

        List<Object> arguments = new ArrayList<>();

        Parameter[] javaParameters = method.getParameters();

        for (Parameter parameter : javaParameters) {
            String pathName = RestUtils.getParameterName(parameter);
            Optional<Pair<String, String>> value = paths.stream().filter(pair -> RestUtils.isParam(pair.getLeft())).filter(s -> Objects.equals(s.getLeft().substring(1), pathName)).findFirst();
            if (value.isPresent()) {
                boolean allowed = parameterConverterManager.checkParameter(value, parameter);
                if (allowed) {
                    arguments.add(value.get().getRight());
                    continue;
                } else {
                    Object convertedParameter = parameterConverterManager.convertParameterUntilFound(value.get().getRight(), parameter);
                    if (convertedParameter == null) {
                        throw new RestMethodInvocationException("Wrong type for parameter " + parameter.getName() + " for REST resource " + restMethod.getPath());
                    }
                    arguments.add(convertedParameter);
                    continue;
                }
            }

            if (parameters != null) {
                if (parameter.getAnnotation(RestQuery.class) != null) {
                    Object queryParameter = null;
                    RestQueryParamsMap map = parameters.getQueryParams();
                    if (map != null) {
                        map = map.get(parameter.getAnnotation(RestQuery.class).value());

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
                        arguments.add(null); // query parameters are always optional
                        continue;
                    }

                    boolean allowed = parameterConverterManager.checkParameter(queryParameter, parameter);
                    if (allowed) {
                        arguments.add(queryParameter);
                        continue;
                    }

                    Object convertedParameter = parameterConverterManager.convertParameterUntilFound(queryParameter, parameter);
                    if (convertedParameter == null) {
                        throw new RestMethodInvocationException("Wrong type for parameter " + parameter.getName() + " for REST resource " + restMethod.getPath());
                    }
                    arguments.add(convertedParameter);
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
            result = restMethod.getMethod().invoke(null, arguments.toArray());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException && e.getCause() instanceof RethrowableException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RestMethodInvocationException("Unable to invoke resource: " + restMethod.getPath() + ": " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
        } catch (Exception e) {
            throw new RestMethodInvocationException("Unable to invoke method " + restMethod.getPath() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
        }

        if (result == null) {
            return null;
        }

        int i = pathParts.size() - resourceParts.size();
        if (i > 0) {// we have at least one operation
            List<RestOperationMethod> operationMethods = new ArrayList<>();

            Class<?> resultClass = result.getClass();

            while (i > 0) {
                String operation = pathParts.get(pathParts.size() - i);

                RestOperationMethod operationMethod = findOperation(resultClass, operation);

                if (operationMethod == null) {
                    // TODO: Search for superclasses/interfaces
                    throw new RestNotFoundException("Unable to find operation " + operation + " on object of type " + result.getClass().getName());
                }

                operationMethods.add(operationMethod);

                resultClass = operationMethod.getJavaMethod().getReturnType();

                i--;
            }

            long postCount = operationMethods.stream().filter(restOperationMethod -> restOperationMethod.getRestMethod() == RestMethod.POST).count();

            if (postCount > 1) {
                throw new RestMethodInvocationException("Unable to invoke 2 methods with operation POST");
            }

            if (postCount == 1 && operationMethods.get(operationMethods.size() - 1).getRestMethod() != RestMethod.POST) {
                throw new RestMethodInvocationException("Cannot have an intermediary POST operation, must be the last operation");
            }

            for (RestOperationMethod operationMethod : operationMethods) {
                if (parameters != null && !parameters.hasPermission(operationMethod)) {
                    throw new RestForbiddenException("No permission to access operation " + operationMethod.getPath());
                }

                if (parameters != null && operationMethod.getRestMethod() != parameters.getMethod()) {
                    throw new RestForbiddenException("Invalid method for operation " + operationMethod.getPath());
                }

                List<Object> operationArguments = new ArrayList<>();
                operationArguments.add(result);

                if (operationMethod.getJavaMethod().getParameterCount() > 1) {
                    Parameter[] javaOperationParameters = operationMethod.getJavaMethod().getParameters();

                    for (int j = 1; j < operationMethod.getJavaMethod().getParameterCount(); j++) {
                        Parameter javaParameter = javaOperationParameters[j];

                        if (javaParameter.getAnnotation(RestBody.class) != null) {
                            Object bodyParameter = null;

                            if (parameters != null) {
                                Object body = parameters.getBody();
                                if (body instanceof JSONObject) {
                                    bodyParameter = ((JSONObject) body).toMap();
                                } else if (body instanceof JSONArray) {
                                    bodyParameter = ((JSONArray) body).toList();
                                } else if (body != null) {
                                    bodyParameter = body;
                                }
                            }

                            boolean allowed = parameterConverterManager.checkParameter(bodyParameter, javaParameter);
                            if (allowed) {
                                operationArguments.add(bodyParameter);
                                continue;
                            }

                            Object convertedParameter = parameterConverterManager.convertParameterUntilFound(bodyParameter, javaParameter);
                            if (convertedParameter == null) {
                                throw new RestMethodInvocationException("Wrong type for body for REST operation " + restMethod.getPath());
                            }
                            operationArguments.add(convertedParameter);
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
                    result = operationMethod.getJavaMethod().invoke(null, operationArguments.toArray());
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException && e.getCause() instanceof RethrowableException) {
                        throw (RuntimeException) e.getCause();
                    }
                    throw new RestMethodInvocationException("Unable to invoke operation: " + operationMethod.getPath() + ": " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
                } catch (Exception e) {
                    throw new RestMethodInvocationException("Unable to invoke method " + operationMethod.getPath() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
                }

                if (result == null) {
                    return null;
                }
            }
        }

        return result;
    }

    public List<RestResourceMethod> findResources(String path) {
        return resources.stream().filter(resource -> resource.matches(path)).collect(Collectors.toList());
    }

    public void registerClass(Class<?> clazz) throws RestMethodRegistrationException {
        for (java.lang.reflect.Method objectMethod : clazz.getMethods()) {
            RestResource resourceAnnotation = objectMethod.getAnnotation(RestResource.class);
            if (resourceAnnotation != null) {
                if (!Modifier.isStatic(objectMethod.getModifiers())) {
                    throw new RestMethodRegistrationException("REST resource methods must be static", objectMethod);
                }

                String path = resourceAnnotation.value();

                List<String> parts = RestUtils.splitPathByParts(path);
                List<String> parameterNames = new ArrayList<>();

                for (Parameter parameter : objectMethod.getParameters()) {
                    if (parameter.getAnnotation(RestPath.class) != null) {
                        String pathName = parameter.getAnnotation(RestPath.class).value();
                        parameterNames.add(pathName);
                        if (!parts.contains(":" + pathName)) {
                            throw new RestMethodRegistrationException("Failed to find path parameter " + pathName, objectMethod);
                        }
                    }
                    if (parameter.getAnnotation(RestBody.class) != null) {
                        throw new RestMethodRegistrationException("A body can only be found on an operation", objectMethod);
                    }
                }

                for (String part : parts) {
                    if (RestUtils.isParam(part)) {
                        if (!parameterNames.contains(part.substring(1))) {
                            throw new RestMethodRegistrationException("Failed to find method parameter " + part, objectMethod);
                        }
                    }
                }

                if (path.isEmpty()) {
                    throw new RestMethodRegistrationException("REST resource paths must not be empty", objectMethod);
                }

                resources.add(new RestResourceMethod(path, objectMethod));
            }

            RestOperation operationAnnotation = objectMethod.getAnnotation(RestOperation.class);
            if (operationAnnotation != null) {
                if (!Modifier.isStatic(objectMethod.getModifiers())) {
                    throw new RestMethodRegistrationException("REST operation methods must be static", objectMethod);
                }

                if (objectMethod.getParameterCount() < 1) {
                    throw new RestMethodRegistrationException("REST operation methods must have at least 1 parameter: ", objectMethod);
                }

                if (!operationAnnotation.value().isAssignableFrom(objectMethod.getParameters()[0].getType())) {
                    throw new RestMethodRegistrationException("REST operation methods must have the operated on class as first parameter", objectMethod);
                }

                String path = operationAnnotation.path();
                RestMethod restMethod = operationAnnotation.method();
                if (path.isEmpty()) {
                    path = objectMethod.getName();
                    if (objectMethod.getName().startsWith("get")) {
                        path = path.substring(3);
                        path = path.substring(0, 1).toLowerCase() + path.substring(1);
                        if (restMethod == RestMethod.DEFAULT) {
                            restMethod = RestMethod.GET;
                        }
                    } else if (objectMethod.getName().startsWith("set")) {
                        path = path.substring(3);
                        path = path.substring(0, 1).toLowerCase() + path.substring(1);
                        if (restMethod == RestMethod.DEFAULT) {
                            restMethod = RestMethod.POST;
                        }
                    } else {
                        throw new RestMethodRegistrationException("Path must be set for method unless the name starts with get or set", objectMethod);
                    }
                }

                boolean foundBody = false;
                for (Parameter parameter : objectMethod.getParameters()) {
                    if (parameter.getAnnotation(RestBody.class) != null) {
                        if (foundBody) {
                            throw new RestMethodRegistrationException("Only 1 @" + RestBody.class.getSimpleName() + " can be specified", objectMethod);
                        }
                        if (restMethod != RestMethod.POST) {
                            if (restMethod == RestMethod.DEFAULT) {
                                restMethod = RestMethod.POST;
                            } else {
                                throw new RestMethodRegistrationException("When a @" + RestBody.class.getSimpleName() + " is specified, the method must be POST", objectMethod);
                            }
                        }
                        foundBody = true;
                    }
                }

                if (restMethod == RestMethod.DEFAULT) {
                    restMethod = RestMethod.GET;
                }

                if (!operations.containsKey(operationAnnotation.value())) {
                    operations.put(operationAnnotation.value(), new HashMap<>());
                }
                operations.get(operationAnnotation.value()).put(path, new RestOperationMethod(operationAnnotation.value(), path, restMethod, objectMethod));
            }
        }
    }

    private Map<String, Object> convertQueryParamsMap(RestQueryParamsMap map) {
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
