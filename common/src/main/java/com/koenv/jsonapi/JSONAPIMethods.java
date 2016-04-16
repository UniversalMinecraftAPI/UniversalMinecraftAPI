package com.koenv.jsonapi;

import com.koenv.jsonapi.methods.APIMethod;
import com.koenv.jsonapi.methods.APINamespace;
import com.koenv.jsonapi.methods.Invoker;
import com.koenv.jsonapi.methods.MethodInvoker;
import com.koenv.jsonapi.util.json.JSONArray;
import com.koenv.jsonapi.util.json.JSONObject;

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

    @APIMethod
    public static JSONObject listMethods() {
        JSONObject json = new JSONObject();

        MethodInvoker methodInvoker = JSONAPI.getInstance().getMethodInvoker();

        JSONArray namespaces = new JSONArray();
        methodInvoker.getNamespaces().values().forEach(map -> map.values().forEach(method -> namespaces.put(MethodInvoker.getMethodDeclaration(method))));

        JSONArray classes = new JSONArray();
        methodInvoker.getClasses().values().forEach(map -> map.values().forEach(method -> classes.put(MethodInvoker.getMethodDeclaration(method))));

        json.put("namespaces", namespaces);
        json.put("classes", classes);

        return json;
    }
}
