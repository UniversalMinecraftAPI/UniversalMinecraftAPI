package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.*;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

@APINamespace("uma")
public class UniversalMinecraftAPIMethods {
    @APIMethod
    @RestResource("uma/invoker")
    public static String getInvoker(Invoker invoker) {
        return invoker.toString();
    }

    @APIMethod
    @RestResource("uma/version")
    public static String getVersion() {
        return UniversalMinecraftAPI.getInstance().getProvider().getUMAVersion();
    }

    @APIMethod
    @RestResource("uma/platform/name")
    public static String getPlatform() {
        return UniversalMinecraftAPI.getInstance().getProvider().getPlatform();
    }

    @APIMethod
    @RestResource("uma/platform/version")
    public static String getPlatformVersion() {
        return UniversalMinecraftAPI.getInstance().getProvider().getPlatformVersion();
    }

    @APIMethod
    @RestResource("uma/methods")
    public static Methods listMethods() {
        JSONObject json = new JSONObject();

        MethodInvoker methodInvoker = UniversalMinecraftAPI.getInstance().getMethodInvoker();

        JSONArray namespaces = new JSONArray();
        methodInvoker.getNamespaces().values().forEach(map -> map.values().forEach(method -> namespaces.put(MethodUtils.getMethodDeclaration(method))));

        JSONArray classes = new JSONArray();
        methodInvoker.getClasses().values().forEach(map -> map.values().forEach(method -> classes.put(MethodUtils.getMethodDeclaration(method))));

        json.put("namespaces", namespaces);
        json.put("classes", classes);

        return new Methods(json);
    }

    @APIMethod
    @RestResource("uma/ping")
    public static String ping() {
        return "pong";
    }

    public static class Methods implements JsonSerializable {
        private JSONObject json;

        public Methods(JSONObject json) {
            this.json = json;
        }

        @Override
        public JSONObject toJson(SerializerManager serializerManager) {
            return json;
        }
    }
}
