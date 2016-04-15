package com.koenv.jsonapi.methods;

public interface InvokeParameters {
    /**
     * Gets an object to use in a method call, should not be an expensive call
     * @param clazz The class of which to get an instance to
     * @return The object of this class
     */
    Object get(Class<?> clazz);

    boolean checkPermission(AbstractMethod method);
}
