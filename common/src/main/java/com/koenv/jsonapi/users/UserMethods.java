package com.koenv.jsonapi.users;

import com.koenv.jsonapi.JSONAPI;
import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.users.model.User;

import java.util.Collection;

@APINamespace("users")
public class UserMethods {
    @APIMethod
    public static Collection<User> getUsers() {
        return JSONAPI.getInstance().getUserManager().getUsers();
    }

    @APIMethod(operatesOn = User.class)
    public static String getUsername(User user) {
        return user.getUsername();
    }
}
