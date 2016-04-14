package com.koenv.jsonapi.users;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.util.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@APINamespace("users")
public class UserMethods {
    @APIMethod
    public static List<JSONObject> getUsers() {
        return JSONAPI.getInstance().getConfiguration().getUsersConfiguration().getUsers().stream().map(section -> {
            JSONObject object = new JSONObject();
            object.put("username", section.getUsername());
            object.put("groups", section.getGroups());
            return object;
        }).collect(Collectors.toList());
    }
}
