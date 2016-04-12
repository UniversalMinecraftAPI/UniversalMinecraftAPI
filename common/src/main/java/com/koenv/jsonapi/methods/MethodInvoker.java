package com.koenv.jsonapi.methods;

import com.koenv.jsonapi.parser.expressions.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Invokes methods from expressions that have been created manually or have been parsed by {@link com.koenv.jsonapi.parser.ExpressionParser}.
 */
public class MethodInvoker {
    /**
     * The default namespace for methods, which can be omitted on an annotation and when calling the method.
     */
    protected static final String DEFAULT_NAMESPACE = "this";

    /**
     * Map of method names that do not operate on objects to their methods.
     */
    protected Map<String, Map<String, NamespacedMethod>> namespaceMethodsMap;
    /**
     * Map of method names that do operate on objects to their methods.
     */
    protected Map<Class<?>, Map<String, ClassMethod>> classMethodsMap;

    /**
     * Map of from and to classes to their {@link ParameterConverter}.
     */
    protected Map<Class<?>, Map<Class<?>, ParameterConverter>> toFromParameterConverterMap;

    /**
     * Parameters that will be converted by Java automatically, such as an `int` to {@link Integer}
     */
    protected static Map<Class<?>, List<Class<?>>> alsoAllowed = new HashMap<>();

    static {
        alsoAllowed.put(Integer.class, Arrays.asList(int.class, long.class, Long.class, short.class, Short.class));
        alsoAllowed.put(int.class, Arrays.asList(Integer.class, long.class, Long.class, short.class, Short.class));
        alsoAllowed.put(Short.class, Arrays.asList(short.class, int.class, Integer.class, long.class, Long.class));
        alsoAllowed.put(short.class, Arrays.asList(Short.class, int.class, Integer.class, long.class, Long.class));
        alsoAllowed.put(Long.class, Arrays.asList(long.class, int.class, Integer.class, short.class, Short.class));
        alsoAllowed.put(long.class, Arrays.asList(Long.class, int.class, Integer.class, short.class, Short.class));
        alsoAllowed.put(Double.class, Collections.singletonList(double.class));
        alsoAllowed.put(double.class, Collections.singletonList(Double.class));
        alsoAllowed.put(Float.class, Collections.singletonList(float.class));
        alsoAllowed.put(float.class, Collections.singletonList(Float.class));
        alsoAllowed.put(Boolean.class, Collections.singletonList(boolean.class));
        alsoAllowed.put(boolean.class, Collections.singletonList(Boolean.class));
    }

    public MethodInvoker() {
        this.namespaceMethodsMap = new HashMap<>();
        this.classMethodsMap = new HashMap<>();
        this.toFromParameterConverterMap = new HashMap<>();
        registerDefaultParameterConverters();
    }

    /**
     * Invokes a method that is in an expression
     *
     * @param expression Valid expression that has been created manually or has been created by {@link com.koenv.jsonapi.parser.ExpressionParser}
     * @return The return of the method, after everything has been called.
     * @throws MethodInvocationException Thrown when the method cannot be invoked
     */
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

    /**
     * Registers methods that have been annotated with {@link APIMethod}.
     * <p/>
     * This method calls {@link #registerMethods(Class)} internally by getting the class of the object passed in.
     *
     * @param object An object that contains methods.
     */
    public void registerMethods(Object object) {
        registerMethods(object.getClass());
    }

    /**
     * Registers methods that have been annotated with {@link APIMethod}.
     *
     * @param clazz Class for which to register methods.
     */
    public void registerMethods(Class<?> clazz) {
        for (java.lang.reflect.Method objectMethod : clazz.getMethods()) {
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

    /**
     * Registers a parameter converter.
     *
     * @param from               From which class to convert
     * @param to                 To which class to convert
     * @param parameterConverter The parameter converter
     * @param <From>             From which class to convert
     * @param <To>               To which class to convert
     */
    public <From, To> void registerParameterConverter(Class<From> from, Class<To> to, ParameterConverter<From, To> parameterConverter) {
        if (toFromParameterConverterMap.get(to) == null) {
            toFromParameterConverterMap.put(to, new HashMap<>());
        }
        toFromParameterConverterMap.get(to).put(from, parameterConverter);
    }

    /**
     * Invokes a single method
     *
     * @param namespace            Namespace of the method. Is usually null when lastResult is not null.
     * @param methodCallExpression The expression of the method call.
     * @param lastResult           The object on which to operate. Is usually null when namespace is not null.
     * @return The result returned by the last method in the methodCallExpression.
     * @throws MethodInvocationException Thrown when the method cannot be invoked
     */
    protected Object invokeMethod(String namespace, MethodCallExpression methodCallExpression, Object lastResult) throws MethodInvocationException {
        Method method = null;
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
            if (classMethods != null) {
                method = getClassMethod(classMethods, methodCallExpression.getMethodName());
            }
            if (method == null) {
                classMethods = getClassMethodsMap(lastResult.getClass().getSuperclass());
                if (classMethods != null) {
                    method = getClassMethod(classMethods, methodCallExpression.getMethodName());
                }
            }
            if (method == null) {
                for (Class<?> interfaceClass : lastResult.getClass().getInterfaces()) {
                    classMethods = getClassMethodsMap(interfaceClass);
                    if (classMethods != null) {
                        method = getClassMethod(classMethods, methodCallExpression.getMethodName());
                    }
                    if (method != null) {
                        break;
                    }
                }
            }

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
            if (expression instanceof BooleanExpression) {
                parameters.add(((BooleanExpression) expression).getValue());
            } else if (expression instanceof DoubleExpression) {
                parameters.add(((DoubleExpression) expression).getValue());
            } else if (expression instanceof IntegerExpression) {
                parameters.add(((IntegerExpression) expression).getValue());
            } else if (expression instanceof StringExpression) {
                parameters.add(((StringExpression) expression).getValue());
            } else if (expression instanceof MethodCallExpression) {
                Object result = invokeMethod(null, (MethodCallExpression) expression, null);
                parameters.add(result);
            } else if (expression instanceof ChainedMethodCallExpression) {
                Object result = invokeMethod(expression);
                parameters.add(result);
            } else {
                throw new MethodInvocationException("Unknown expression: " + expression);
            }
        }

        if (method.getJavaMethod().getParameterCount() != parameters.size()) {
            throw new MethodInvocationException("Method " + getMethodDeclaration(method) + " requires " +
                    method.getJavaMethod().getParameterCount() +
                    " parameters, received " + parameters.size());
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

    /**
     * Checks whether an object is valid for a Java parameter
     *
     * @param parameter     Desired parameter to method
     * @param javaParameter The parameter of the method
     * @return Whether this object can be passed to the parameter as is.
     */
    protected boolean checkParameter(Object parameter, Parameter javaParameter) {
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

    /**
     * Converts a parameter until a valid parameter is found as specified by {@link #checkParameter(Object, Parameter)}
     *
     * @param parameter     Desired parameter of the method
     * @param javaParameter The parameter of the method
     * @return Null if this parameter cannot be converted to the parameter type of the method. The converted parameter otherwise.
     */
    protected Object convertParameterUntilFound(Object parameter, Parameter javaParameter) {
        boolean allowed = checkParameter(parameter, javaParameter);
        Object previousParameter;
        while (!allowed) {
            previousParameter = parameter;
            parameter = convertParameter(parameter, javaParameter.getType(), null);
            if (parameter == null) {
                parameter = convertParameter(previousParameter, javaParameter.getType(), previousParameter.getClass().getSuperclass());
                if (parameter == null) {
                    for (Class<?> interfaceClass : previousParameter.getClass().getInterfaces()) {
                        parameter = convertParameter(previousParameter, javaParameter.getType(), interfaceClass);
                        if (parameter != null) {
                            break;
                        }
                    }
                    if (parameter == null) {
                        return null;
                    }
                }
            }
            allowed = checkParameter(parameter, javaParameter);
        }
        return parameter;
    }

    /**
     * Converts a parameter from one type to another
     *
     * @param parameter The parameter to convert
     * @param to        To which class to convert
     * @param from      From which class to convert, which is usually the class of from, from's superclass or one of from's interfaces
     * @return The converted parameter. Null if it cannot be converted.
     */
    protected Object convertParameter(Object parameter, Class<?> to, Class<?> from) {
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

    /**
     * Registers a single namespaced method into {@link #namespaceMethodsMap}
     *
     * @param method Method to register
     */
    protected void registerMethod(NamespacedMethod method) {
        if (namespaceMethodsMap.get(method.getNamespace()) == null) {
            namespaceMethodsMap.put(method.getNamespace(), new HashMap<>());
        }
        namespaceMethodsMap.get(method.getNamespace()).put(method.getName(), method);
    }

    /**
     * Registers a single method which operates on an object into {@link #classMethodsMap}
     *
     * @param method Method to register
     */
    protected void registerClassMethod(ClassMethod method) {
        if (classMethodsMap.get(method.getOperatesOn()) == null) {
            classMethodsMap.put(method.getOperatesOn(), new HashMap<>());
        }
        classMethodsMap.get(method.getOperatesOn()).put(method.getName(), method);
    }

    /**
     * Gets the actual namespace name.
     * <p/>
     * If the name of the namespace is empty, this returns {@link #DEFAULT_NAMESPACE}
     *
     * @param namespace Namespace name
     * @return The actual namespace name
     */
    protected String getNamespaceName(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return DEFAULT_NAMESPACE;
        }
        return namespace;
    }

    /**
     * Gets a namespace from the {@link #namespaceMethodsMap}
     *
     * @param namespace Actual namespace name, as converted by {@link #getNamespaceName(String)}
     * @return A map of methods in the namespace and the methods itself
     */
    protected Map<String, NamespacedMethod> getNamespace(String namespace) {
        return namespaceMethodsMap.get(getNamespaceName(namespace));
    }

    /**
     * Gets a method from a namespace.
     *
     * @param methods    Usually the result of {@link #getNamespace(String)}
     * @param methodName Name of the desired method
     * @return The method if found, null otherwise
     */
    protected NamespacedMethod getMethod(Map<String, NamespacedMethod> methods, String methodName) {
        return methods.get(methodName);
    }

    /**
     * Gets methods for the operated on class.
     *
     * @param operatesOn Class for which to return methods
     * @return A map of method names to the actual methods
     */
    protected Map<String, ClassMethod> getClassMethodsMap(Class<?> operatesOn) {
        return classMethodsMap.get(operatesOn);
    }

    /**
     * Gets a method from a map of method names to methods.
     *
     * @param methods    Usually the result of {@link #getClassMethodsMap(Class)}
     * @param methodName Name of the desired method
     * @return The method if found, null otherwise
     */
    protected ClassMethod getClassMethod(Map<String, ClassMethod> methods, String methodName) {
        return methods.get(methodName);
    }

    /**
     * Gets a method declaration. This will be in the format: `methodName(methodParameters)`
     *
     * @param method Method for which to get the method declaration.
     * @return A string representation of the method.
     */
    protected String getMethodDeclaration(Method method) {
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

    /**
     * Registers all default parameter converters, such as from float to double and the reverse.
     */
    protected void registerDefaultParameterConverters() {
        ParameterConverter<Double, Float> doubleToFloatConverter = Double::floatValue;
        ParameterConverter<Float, Double> floatToDoubleConverter = Float::doubleValue;
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
