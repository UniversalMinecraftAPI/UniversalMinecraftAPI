package com.koenv.jsonapi.serializer;

import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.serializer.defaults.CollectionSerializer;
import com.koenv.jsonapi.serializer.defaults.JsonResponseSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultSerializers {
    public static final Map<Class<?>, Serializer<?>> SERIALIZERS;

    static final Serializer<Boolean> BOOLEAN_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Byte> BYTE_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Character> CHARACTER_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Double> DOUBLE_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Float> FLOAT_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Integer> INTEGER_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Long> LONG_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<Short> SHORT_SERIALIZER = (object, serializerManager) -> object.toString();
    static final Serializer<String> STRING_SERIALIZER = (object, serializerManager) -> object;

    static {
        SERIALIZERS = new HashMap<>();
        SERIALIZERS.put(JsonResponse.class, new JsonResponseSerializer());
        SERIALIZERS.put(Collection.class, new CollectionSerializer());
        SERIALIZERS.put(boolean.class, BOOLEAN_SERIALIZER);
        SERIALIZERS.put(Boolean.class, BOOLEAN_SERIALIZER);
        SERIALIZERS.put(byte.class, BYTE_SERIALIZER);
        SERIALIZERS.put(Byte.class, BYTE_SERIALIZER);
        SERIALIZERS.put(char.class, CHARACTER_SERIALIZER);
        SERIALIZERS.put(Character.class, CHARACTER_SERIALIZER);
        SERIALIZERS.put(double.class, DOUBLE_SERIALIZER);
        SERIALIZERS.put(Double.class, DOUBLE_SERIALIZER);
        SERIALIZERS.put(float.class, FLOAT_SERIALIZER);
        SERIALIZERS.put(Float.class, FLOAT_SERIALIZER);
        SERIALIZERS.put(int.class, INTEGER_SERIALIZER);
        SERIALIZERS.put(Integer.class, INTEGER_SERIALIZER);
        SERIALIZERS.put(long.class, LONG_SERIALIZER);
        SERIALIZERS.put(Long.class, LONG_SERIALIZER);
        SERIALIZERS.put(short.class, SHORT_SERIALIZER);
        SERIALIZERS.put(Short.class, SHORT_SERIALIZER);
        SERIALIZERS.put(String.class, STRING_SERIALIZER);
    }

    public static void register(SerializerManager manager) {
        for (Map.Entry<Class<?>, Serializer<?>> entry : SERIALIZERS.entrySet()) {
            //noinspection unchecked
            manager.registerSerializer((Class<Object>) entry.getKey(), (Serializer<Object>) entry.getValue());
        }
    }
}
