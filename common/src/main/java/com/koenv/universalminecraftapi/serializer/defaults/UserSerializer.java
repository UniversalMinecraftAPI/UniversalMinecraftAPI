package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.json.JSONObject;

public class UserSerializer implements Serializer<User> {
    @Override
    public Object toJson(User object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("username", object.getUsername());
        json.put("groups", object.getGroups());
        return json;
    }
}
