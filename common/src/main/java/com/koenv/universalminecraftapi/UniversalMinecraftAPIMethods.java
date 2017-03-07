package com.koenv.universalminecraftapi;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.http.rest.RestResource;
import com.koenv.universalminecraftapi.methods.*;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

import java.util.ArrayList;
import java.util.List;

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

        List<String> namespaces = new ArrayList<>();
        methodInvoker.getNamespaces().values().forEach(map -> map.values().forEach(method -> namespaces.add(MethodUtils.getMethodDeclaration(method))));

        List<String> classes = new ArrayList<>();
        methodInvoker.getClasses().values().forEach(map -> map.values().forEach(method -> classes.add(MethodUtils.getMethodDeclaration(method))));

        return new Methods(namespaces, classes);
    }

    @APIMethod
    @RestResource("uma/ping")
    public static String ping() {
        return "pong";
    }

    public static class Methods implements JsonSerializable {
        private List<String> namespaces;
        private List<String> classes;

        Methods(List<String> namespaces, List<String> classes) {
            this.namespaces = namespaces;
            this.classes = classes;
        }

        @Override
        public void toJson(JSONWriter writer, SerializerManager serializerManager) {
            writer.object();

            writer.key("namespaces");
            serializerManager.serialize(namespaces, writer);

            writer.key("classes");
            serializerManager.serialize(classes, writer);

            writer.endObject();
        }
    }
}
