package com.koenv.universalminecraftapi.users;

import com.koenv.universalminecraftapi.ErrorCodes;
import com.koenv.universalminecraftapi.UniversalMinecraftAPI;
import com.koenv.universalminecraftapi.http.model.APIException;
import com.koenv.universalminecraftapi.http.model.BaseHttpInvoker;
import com.koenv.universalminecraftapi.http.rest.RestOperation;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.APIMethod;
import com.koenv.universalminecraftapi.methods.APINamespace;
import com.koenv.universalminecraftapi.methods.Invoker;
import com.koenv.universalminecraftapi.users.model.User;

import java.util.Collection;

@APINamespace("users")
public class UserMethods {
    @APIMethod
    @RestResource("users")
    public static Collection<User> getUsers() {
        return UniversalMinecraftAPI.getInstance().getUserManager().getUsers();
    }

    @APIMethod(operatesOn = User.class)
    @RestOperation(User.class)
    public static String getUsername(User user) {
        return user.getUsername();
    }

    @APIMethod
    @RestResource("users/apikey")
    public static String generateApiKey(Invoker invoker) {
        if (invoker instanceof BaseHttpInvoker) {
            BaseHttpInvoker httpInvoker = (BaseHttpInvoker) invoker;
            return UniversalMinecraftAPI.getInstance().getUserManager().getApiKeyManager().generate(httpInvoker.getUser());
        }
        throw new APIException("Invalid user", ErrorCodes.INVALID_CREDENTIALS);
    }

    @APIMethod
    @RestResource("users/current")
    public static User getCurrentUser(Invoker invoker) {
        if (invoker instanceof BaseHttpInvoker) {
            BaseHttpInvoker httpInvoker = (BaseHttpInvoker) invoker;
            return httpInvoker.getUser();
        }
        throw new APIException("Invalid user", ErrorCodes.INVALID_CREDENTIALS);
    }
}
