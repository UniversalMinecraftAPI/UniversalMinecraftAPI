package com.koenv.jsonapi.methods;

public interface ParameterConverter<From, To> {
    To convert(From from);
}
