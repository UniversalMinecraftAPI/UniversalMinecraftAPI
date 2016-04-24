package com.koenv.universalminecraftapi.http.rest;

import com.koenv.universalminecraftapi.methods.RethrowableException;
import com.koenv.universalminecraftapi.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class RestHandler {
    private List<RestResourceMethod> resources = new ArrayList<>();
    private Map<Class<?>, Map<String, RestOperationMethod>> operations = new HashMap<>();

    public Object handle(String path, RestParameters parameters) throws RestException {
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
                arguments.add(value.get().getRight());
            }
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

        int i = pathParts.size() - resourceParts.size();
        if (i > 0) {// we have an operation
            while (i > 0) {
                String operation = pathParts.get(pathParts.size() - i);

                RestOperationMethod operationMethod = operations.get(result.getClass()).get(operation);

                if (operationMethod == null) {
                    throw new RestNotFoundException("Unable to find operation " + operation + " on object of type " + result.getClass().getName());
                }

                List<Object> operationArguments = new ArrayList<>();
                operationArguments.add(result);

                try {
                    result = operationMethod.getJavaMethod().invoke(null, operationArguments.toArray());
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof RuntimeException && e.getCause() instanceof RethrowableException) {
                        throw (RuntimeException) e.getCause();
                    }
                    throw new RestMethodInvocationException("Unable to invoke operation: " + restMethod.getPath() + ": " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
                } catch (Exception e) {
                    throw new RestMethodInvocationException("Unable to invoke method " + restMethod.getPath() + ": " + e.getClass().getSimpleName() + " " + e.getMessage(), e);
                }

                i--;
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
                List<String> parameterNames = Arrays.asList(objectMethod.getParameters()).stream().map(RestUtils::getParameterName).collect(Collectors.toList());

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

                if (restMethod == RestMethod.DEFAULT) {
                    restMethod = RestMethod.GET;
                }

                boolean foundBody = false;
                for (Parameter parameter : objectMethod.getParameters()) {
                    if (parameter.getAnnotation(RestBody.class) != null) {
                        if (foundBody) {
                            throw new RestMethodRegistrationException("Only 1 @" + RestBody.class.getSimpleName() + " can be specified", objectMethod);
                        }
                        if (restMethod != RestMethod.POST) {
                            throw new RestMethodRegistrationException("When a @" + RestBody.class.getSimpleName() + " is specified, the method must be POST", objectMethod);
                        }
                        foundBody = true;
                    }
                }

                if (!operations.containsKey(operationAnnotation.value())) {
                    operations.put(operationAnnotation.value(), new HashMap<>());
                }
                operations.get(operationAnnotation.value()).put(path, new RestOperationMethod(operationAnnotation.value(), path, restMethod, objectMethod));
            }
        }
    }

    public List<RestResourceMethod> getResources() {
        return resources;
    }

    public Map<Class<?>, Map<String, RestOperationMethod>> getOperations() {
        return operations;
    }
}
