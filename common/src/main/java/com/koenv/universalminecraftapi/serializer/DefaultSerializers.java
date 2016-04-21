package com.koenv.universalminecraftapi.serializer;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.defaults.CollectionSerializer;
import com.koenv.universalminecraftapi.serializer.defaults.JsonResponseSerializer;
import com.koenv.universalminecraftapi.serializer.defaults.UserSerializer;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultSerializers {
    public static final Map<Class<?>, Serializer<?>> SERIALIZERS;

    static final Serializer<Object> DEFAULT_SERIALIZER = (object, serializerManager) -> object;

    static {
        SERIALIZERS = new HashMap<>();
        SERIALIZERS.put(JsonSerializable.class, new JsonResponseSerializer());
        SERIALIZERS.put(Collection.class, new CollectionSerializer());
        SERIALIZERS.put(User.class, new UserSerializer());
        SERIALIZERS.put(boolean.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Boolean.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(byte.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Byte.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(char.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Character.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(double.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Double.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(float.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Float.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(int.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Integer.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(long.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Long.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(short.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(Short.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(String.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(JSONObject.class, DEFAULT_SERIALIZER);
        SERIALIZERS.put(JSONArray.class, DEFAULT_SERIALIZER);
    }

    public static void register(SerializerManager manager) {
        for (Map.Entry<Class<?>, Serializer<?>> entry : SERIALIZERS.entrySet()) {
            //noinspection unchecked
            manager.registerSerializer((Class<Object>) entry.getKey(), (Serializer<Object>) entry.getValue());
        }
    }
}
