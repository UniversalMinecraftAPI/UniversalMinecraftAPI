package com.koenv.jsonapi.methods;

/**
 * Converts parameters from one type to another
 * @param <From> From which to convert this class
 * @param <To> To which type this class will be converted
 */
public interface ParameterConverter<From, To> {
    To convert(From from);
}
