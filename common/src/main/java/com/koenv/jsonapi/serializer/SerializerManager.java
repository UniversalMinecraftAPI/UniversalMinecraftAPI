package com.koenv.jsonapi.serializer;

import com.koenv.jsonapi.util.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SerializerManager {
    private Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    public Object serialize(Object object) {
        if (object == null) {
            return JSONObject.NULL;
        }
        return serialize(object, object.getClass());
    }

    private <T> Object serialize(T object, Class<? extends T> clazz) {
        return serialize(object, clazz, false);
    }

    private <T> Object serialize(T object, Class<? extends T> clazz, boolean within) {
        if (serializers.containsKey(clazz)) {
            Serializer<T> serializer = (Serializer<T>) serializers.get(clazz);
            return serializer.toJson(object, this);
        }
        if (clazz.getSuperclass() != null) {
            Object value = serialize(object, clazz.getSuperclass(), true);
            if (value == null) {
                for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                    Object interfaceValue = serialize(object, interfaceClazz, true);
                    if (interfaceValue != null) {
                        return interfaceValue;
                    }
                }
            } else {
                return value;
            }
        } else {
            for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                Object interfaceValue = serialize(object, interfaceClazz, true);
                if (interfaceValue != null) {
                    return interfaceValue;
                }
            }
        }
        if (!within) {
            JSONObject alt = new JSONObject();
            alt.put("type", object.getClass().getSimpleName());
            alt.put("representation", object.toString());
            alt.put("error", "not_serializable");
            return alt;
        }
        return null;
    }

    public <T> void registerSerializer(Class<T> clazz, Serializer<T> serializer) {
        serializers.put(clazz, serializer);
    }
}
