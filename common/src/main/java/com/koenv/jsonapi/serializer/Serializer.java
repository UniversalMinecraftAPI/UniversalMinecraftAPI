package com.koenv.jsonapi.serializer;

public interface Serializer<T> {
    Object toJson(T object, SerializerManager serializerManager);
}
