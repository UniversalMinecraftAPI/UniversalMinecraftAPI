package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.parser.expressions.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class MethodInvoker {
    private static final String DEFAULT_NAMESPACE = "this";

    private Map<String, Map<String, NamespacedMethod>> namespaceMethodsMap;
    private Map<Class<?>, Map<String, ClassMethod>> classMethodsMap;

    private Map<Class<?>, Map<Class<?>, ParameterConverter>> toFromParameterConverterMap;

    private static Map<Class<?>, List<Class<?>>> alsoAllowed = new HashMap<>();

    static {
        alsoAllowed.put(Integer.class, Arrays.asList(int.class, long.class, Long.class, short.class, Short.class));
        alsoAllowed.put(int.class, Arrays.asList(Integer.class, long.class, Long.class, short.class, Short.class));
        alsoAllowed.put(Short.class, Arrays.asList(short.class, int.class, Integer.class, long.class, Long.class));
        alsoAllowed.put(short.class, Arrays.asList(Short.class, int.class, Integer.class, long.class, Long.class));
        alsoAllowed.put(Long.class, Arrays.asList(long.class, int.class, Integer.class, short.class, Short.class));
        alsoAllowed.put(long.class, Arrays.asList(Long.class, int.class, Integer.class, short.class, Short.class));
        alsoAllowed.put(Double.class, Arrays.asList(double.class));
        alsoAllowed.put(double.class, Arrays.asList(Double.class));
        alsoAllowed.put(Float.class, Arrays.asList(float.class));
        alsoAllowed.put(float.class, Arrays.asList(Float.class));
        alsoAllowed.put(Boolean.class, Arrays.asList(boolean.class));
        alsoAllowed.put(boolean.class, Arrays.asList(Boolean.class));
    }

    public MethodInvoker() {
        this.namespaceMethodsMap = new HashMap<>();
        this.classMethodsMap = new HashMap<>();
        this.toFromParameterConverterMap = new HashMap<>();
        registerDefaultParameterConverters();
    }

    public Object invokeMethod(Expression expression) throws MethodInvocationException {
        if (expression instanceof ChainedMethodCallExpression) {
            List<Expression> expressions = ((ChainedMethodCallExpression) expression).getExpressions();
            String namespace = null;
            Object lastResult = null;
            for (int i = 0; i < expressions.size(); i++) {
                Expression innerExpression = expressions.get(i);
                if (innerExpression instanceof NamespaceExpression) {
                    if (i != 0) {
                        throw new MethodInvocationException("Namespace can only be in first position");
                    }
                    namespace = ((NamespaceExpression) innerExpression).getName();
                } else if (innerExpression instanceof MethodCallExpression) {
                    if (i == 0 || (i == 1 && namespace != null)) {
                        lastResult = invokeMethod(namespace, (MethodCallExpression) innerExpression, lastResult);
                    } else {
                        lastResult = invokeMethod(null, (MethodCallExpression) innerExpression, lastResult);
                    }
                }
            }
            return lastResult;
        } else if (expression instanceof MethodCallExpression) {
            return invokeMethod(null, (MethodCallExpression) expression, null);
        } else if (expression instanceof ValueExpression) {
            return ((ValueExpression) expression).getValue();
        } else {
            throw new MethodInvocationException("Unable to invoke non-existent expression");
        }
    }

    public void registerMethods(Object object) {
        for (java.lang.reflect.Method objectMethod : object.getClass().getMethods()) {
            APIMethod annotation = objectMethod.getAnnotation(APIMethod.class);
            if (annotation == null) {
                continue;
            }
            if (!Modifier.isPublic(objectMethod.getModifiers())) {
                throw new MethodRegistrationException("All registered API methods must be public: " + objectMethod.getName());
            }
            if (!Modifier.isStatic(objectMethod.getModifiers())) {
                throw new MethodRegistrationException("All registered API methods must be static: " + objectMethod.getName());
            }
            if (!APIMethod.DEFAULT.class.equals(annotation.operatesOn())) {
                if (objectMethod.getParameterCount() < 1) {
                    throw new MethodRegistrationException("API methods which operate on an object need to have at least 1 parameter: " + objectMethod.getName());
                }
                if (!annotation.operatesOn().isAssignableFrom(objectMethod.getParameters()[0].getType())) {
                    throw new MethodRegistrationException("API methods which operate on an object need to have the operatesOn class as first parameter: " + objectMethod.getName());
                }
                ClassMethod classMethod = new ClassMethod(annotation.operatesOn(), objectMethod.getName(), objectMethod);
                registerClassMethod(classMethod);
                continue;
            }
            String namespace = getNamespaceName(annotation.namespace());
            NamespacedMethod method = new NamespacedMethod(namespace, objectMethod.getName(), objectMethod);
            registerMethod(method);
        }
    }

    public <From, To> void registerParameterConverter(Class<From> from, Class<To> to, ParameterConverter<From, To> parameterConverter) {
        if (toFromParameterConverterMap.get(to) == null) {
            toFromParameterConverterMap.put(to, new HashMap<>());
        }
        toFromParameterConverterMap.get(to).put(from, parameterConverter);
    }

    private Object invokeMethod(String namespace, MethodCallExpression methodCallExpression, Object lastResult) throws MethodInvocationException {
        Method method;
        if (lastResult == null) {
            Map<String, NamespacedMethod> namespaceMethods = getNamespace(namespace);
            if (namespaceMethods == null) {
                throw new MethodInvocationException("Unable to find namespace " + getNamespaceName(namespace));
            }
            method = getMethod(namespaceMethods, methodCallExpression.getMethodName());
            if (method == null) {
                throw new MethodInvocationException("Unable to find method " + methodCallExpression.getMethodName() + " in namespace " + getNamespaceName(namespace));
            }
        } else {
            Map<String, ClassMethod> classMethods = getClassMethodsMap(lastResult.getClass());
            if (classMethods == null) {
                throw new MethodInvocationException("No methods registered for class " + lastResult.getClass().getName());
            }
            method = getClassMethod(classMethods, methodCallExpression.getMethodName());
            if (method == null) {
                throw new MethodInvocationException("No method named " + methodCallExpression.getMethodName() + " for class " + lastResult.getClass().getName());
            }
        }
        List<Object> parameters = new ArrayList<>();

        if (method instanceof ClassMethod) {
            parameters.add(lastResult);
        }

        for (int i = 0; i < methodCallExpression.getParameters().size(); i++) {
            Expression expression = methodCallExpression.getParameters().get(i);
            if (expression instanceof DoubleExpression) {
                parameters.add(((DoubleExpression) expression).getValue());
            } else if (expression instanceof IntegerExpression) {
                parameters.add(((IntegerExpression) expression).getValue());
            } else if (expression instanceof StringExpression) {
                parameters.add(((StringExpression) expression).getValue());
            } else if (expression instanceof MethodCallExpression) {
                Object result = invokeMethod(null, (MethodCallExpression) expression, null);
                parameters.add(result);
            } else if (expression instanceof ChainedMethodCallExpression) {
                Object result = invokeMethod(((ChainedMethodCallExpression) expression));
                parameters.add(result);
            }
        }

        Parameter[] javaParameters = method.getJavaMethod().getParameters();
        List<Object> convertedParameters = new ArrayList<>();

        for (int i = 0; i < javaParameters.length; i++) {
            Parameter javaParameter = javaParameters[i];
            Object parameter = parameters.get(i);
            boolean allowed = checkParameter(parameter, javaParameter);
            if (!allowed) {
                Object convertedParameter = convertParameterUntilFound(parameter, javaParameter);
                if (convertedParameter == null) {
                    throw new MethodInvocationException(
                            "Wrong type of parameter for place " + i +
                                    ": got " + parameters.get(i).getClass().getSimpleName() +
                                    ", expected " + javaParameter.getType().getSimpleName());
                }
                convertedParameters.add(convertedParameter);
                continue;
            }
            convertedParameters.add(parameter);
        }

        Object result;

        try {
            result = method.getJavaMethod().invoke(null, convertedParameters.toArray());
        } catch (Exception e) {
            throw new MethodInvocationException("Unable to invoke method " + getMethodDeclaration(method), e);
        }

        return result;
    }

    private boolean checkParameter(Object parameter, Parameter javaParameter) {
        if (javaParameter.getType().isInstance(parameter)) {
            return true;
        }
        if (alsoAllowed.containsKey(javaParameter.getType())) {
            for (Class<?> alsoAllowedClass : alsoAllowed.get(javaParameter.getType())) {
                if (alsoAllowedClass.isInstance(parameter)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Object convertParameterUntilFound(Object parameter, Parameter javaParameter) {
        boolean allowed = checkParameter(parameter, javaParameter);
        Object previousParameter;
        while (!allowed) {
            previousParameter = parameter;
            parameter = convertParameter(parameter, javaParameter.getType(), null);
            if (parameter == null) {
                parameter = convertParameter(previousParameter, javaParameter.getType(), previousParameter.getClass().getSuperclass());
                if (parameter == null) {
                    return null;
                }
            }
            allowed = checkParameter(parameter, javaParameter);
        }
        return parameter;
    }

    private Object convertParameter(Object parameter, Class<?> to, Class<?> from) {
        if (from == null && parameter != null) {
            from = parameter.getClass();
        }
        Map<Class<?>, ParameterConverter> toParameterMap = toFromParameterConverterMap.get(to);
        if (toParameterMap != null) {
            ParameterConverter parameterConverter = toParameterMap.get(from);
            if (parameterConverter != null) {
                return parameterConverter.convert(parameter);
            }
        }
        return null;
    }

    private void registerMethod(NamespacedMethod method) {
        if (namespaceMethodsMap.get(method.getNamespace()) == null) {
            namespaceMethodsMap.put(method.getNamespace(), new HashMap<>());
        }
        namespaceMethodsMap.get(method.getNamespace()).put(method.getName(), method);
    }

    private void registerClassMethod(ClassMethod method) {
        if (classMethodsMap.get(method.getOperatesOn()) == null) {
            classMethodsMap.put(method.getOperatesOn(), new HashMap<>());
        }
        classMethodsMap.get(method.getOperatesOn()).put(method.getName(), method);
    }

    private String getNamespaceName(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return DEFAULT_NAMESPACE;
        }
        return namespace;
    }

    private Map<String, NamespacedMethod> getNamespace(String namespace) {
        return namespaceMethodsMap.get(getNamespaceName(namespace));
    }

    private NamespacedMethod getMethod(Map<String, NamespacedMethod> methods, String methodName) {
        return methods.get(methodName);
    }

    private Map<String, ClassMethod> getClassMethodsMap(Class<?> operatesOn) {
        return classMethodsMap.get(operatesOn);
    }

    private ClassMethod getClassMethod(Map<String, ClassMethod> methods, String methodName) {
        return methods.get(methodName);
    }

    private String getMethodDeclaration(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        if (method instanceof NamespacedMethod) {
            stringBuilder.append(((NamespacedMethod) method).getNamespace());
        } else if (method instanceof ClassMethod) {
            stringBuilder.append("<");
            stringBuilder.append(((ClassMethod) method).getOperatesOn().getSimpleName());
            stringBuilder.append(">");
        }
        stringBuilder.append(".");
        stringBuilder.append(method.getName());
        stringBuilder.append("(");
        Parameter[] parameters = method.getJavaMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            stringBuilder.append(parameter.getType().getSimpleName());
            if (i != parameters.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private void registerDefaultParameterConverters() {
        ParameterConverter<Double, Float> doubleToFloatConverter = new ParameterConverter<Double, Float>() {
            @Override
            public Float convert(Double aDouble) {
                return aDouble.floatValue();
            }
        };
        ParameterConverter<Float, Double> floatToDoubleConverter = new ParameterConverter<Float, Double>() {
            @Override
            public Double convert(Float aFloat) {
                return aFloat.doubleValue();
            }
        };
        registerParameterConverter(double.class, float.class, doubleToFloatConverter);
        registerParameterConverter(double.class, Float.class, doubleToFloatConverter);
        registerParameterConverter(Double.class, float.class, doubleToFloatConverter);
        registerParameterConverter(Double.class, Float.class, doubleToFloatConverter);

        registerParameterConverter(float.class, double.class, floatToDoubleConverter);
        registerParameterConverter(float.class, Double.class, floatToDoubleConverter);
        registerParameterConverter(Float.class, double.class, floatToDoubleConverter);
        registerParameterConverter(Float.class, Double.class, floatToDoubleConverter);
    }
}
