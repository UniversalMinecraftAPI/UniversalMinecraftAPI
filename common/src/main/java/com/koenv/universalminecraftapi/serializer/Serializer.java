package com.koenv.universalminecraftapi.serializer;

public interface Serializer<T> {
    Object toJson(T object, SerializerManager serializerManager);
}
