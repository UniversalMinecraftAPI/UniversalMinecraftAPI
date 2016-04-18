package com.koenv.jsonapi.serializer.defaults;

import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.users.model.User;
import com.koenv.jsonapi.util.json.JSONObject;

public class UserSerializer implements Serializer<User> {
    @Override
    public Object toJson(User object, SerializerManager serializerManager) {
        JSONObject json = new JSONObject();
        json.put("username", object.getUsername());
        json.put("groups", object.getGroups());
        return json;
    }
}
