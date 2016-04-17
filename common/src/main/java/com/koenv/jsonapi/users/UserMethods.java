package com.koenv.jsonapi.users;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.http.model.JsonSerializable;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@APINamespace("users")
public class UserMethods {
    @APIMethod
    public static List<User> getUsers() {
        return JSONAPI.getInstance().getUserManager().getUsers().stream().map(user -> {
            JSONObject object = new JSONObject();
            object.put("username", user.getUsername());
            object.put("groups", user.getGroups());
            return new User(object);
        }).collect(Collectors.toList());
    }

    public static class User implements JsonSerializable {
        private JSONObject json;

        public User(JSONObject json) {
            this.json = json;
        }

        @Override
        public JSONObject toJson(SerializerManager serializerManager) {
            return json;
        }
    }
}
