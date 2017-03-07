package com.koenv.universalminecraftapi.serializer;

import com.koenv.universalminecraftapi.util.json.JSONObject;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class SerializerManager {
    private Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    public String serialize(Object object) {
        if (object == null) {
            return JSONObject.NULL.toString();
        }

        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);

        boolean result = serialize(object, object.getClass(), writer);

        if (!result) {
            writer.object()
                    .key("type").value(object.getClass().getSimpleName())
                    .key("representation").value(object.toString())
                    .key("error").value("not_serializable")
                    .endObject();
        }

        return stringWriter.toString();
    }

    public <T> void serialize(T object, JSONWriter writer) {
        boolean result = serialize(object, object.getClass(), writer);

        if (!result) {
            writer.object()
                    .key("type").value(object.getClass().getSimpleName())
                    .key("representation").value(object.toString())
                    .key("error").value("not_serializable")
                    .endObject();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> boolean serialize(T object, Class<? extends T> clazz, JSONWriter writer) {
        if (serializers.containsKey(clazz)) {
            Serializer<T> serializer = (Serializer<T>) serializers.get(clazz);
            serializer.toJson(object, this, writer);

            return true;
        }

        if (clazz.getSuperclass() != null) {
            if (serialize(object, clazz.getSuperclass(), writer)) {
                return true;
            }
        }

        for (Class<?> interfaceClazz : clazz.getInterfaces()) {
            if (serialize(object, interfaceClazz, writer)) {
                return true;
            }
        }

        return false;
    }

    public <T> void registerSerializer(Class<T> clazz, Serializer<T> serializer) {
        serializers.put(clazz, serializer);
    }
}
