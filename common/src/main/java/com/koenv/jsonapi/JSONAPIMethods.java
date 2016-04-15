package com.koenv.jsonapi;

import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.methods.Invoker;

@APINamespace("jsonapi")
public class JSONAPIMethods {
    @APIMethod
    public static String getInvoker(Invoker invoker) {
        return invoker.toString();
    }

    @APIMethod
    public static String getVersion() {
        return JSONAPI.getInstance().getProvider().getJSONAPIVersion();
    }

    @APIMethod
    public static String getPlatform() {
        return JSONAPI.getInstance().getProvider().getPlatform();
    }

    @APIMethod
    public static String getPlatformVersion() {
        return JSONAPI.getInstance().getProvider().getPlatformVersion();
    }
}
