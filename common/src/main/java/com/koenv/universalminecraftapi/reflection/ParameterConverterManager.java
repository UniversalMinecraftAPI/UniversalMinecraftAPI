package com.koenv.universalminecraftapi.reflection;

import java.lang.reflect.Parameter;
import java.util.*;

public class ParameterConverterManager {
    /**
     * Map of from and to classes to their {@link ParameterConverter}.
     */
    protected Map<Class<?>, Map<Class<?>, ParameterConverter>> toFromParameterConverterMap = new HashMap<>();

    /**
     * Parameters that will be converted by Java automatically, such as an `int` to {@link Integer}
     */
    protected Map<Class<?>, List<Class<?>>> alsoAllowed = new HashMap<>();

    public ParameterConverterManager() {
        registerDefaultParameterConverters();
        registerDefaultAlsoAllowed();
    }

    private void registerDefaultParameterConverters() {
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

    private void registerDefaultAlsoAllowed() {
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

    /**
     * Checks whether an object is valid for a Java parameter
     *
     * @param parameter     Desired parameter to method
     * @param javaParameter The parameter of the method
     * @return Whether this object can be passed to the parameter as is.
     */
    public boolean checkParameter(Object parameter, Parameter javaParameter) {
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
    public Object convertParameterUntilFound(Object parameter, Parameter javaParameter) {
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
    @SuppressWarnings("unchecked")
    public Object convertParameter(Object parameter, Class<?> to, Class<?> from) {
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
}
