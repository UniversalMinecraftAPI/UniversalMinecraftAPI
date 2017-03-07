package com.koenv.universalminecraftapi.serializer.defaults;

import com.koenv.universalminecraftapi.serializer.Serializer;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.users.model.User;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

public class UserSerializer implements Serializer<User> {
    @Override
    public void toJson(User object, SerializerManager serializerManager, JSONWriter writer) {
        writer.object()
                .key("username").value(object.getUsername())
                .key("groups").value(object.getGroups())
                .endObject();
    }
}
