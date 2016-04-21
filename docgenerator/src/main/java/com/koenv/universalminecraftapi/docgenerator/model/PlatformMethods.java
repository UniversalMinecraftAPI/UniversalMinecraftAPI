package com.koenv.universalminecraftapi.docgenerator.model;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlatformMethods {
    private Platform platform;
    private List<NamespacedMethod> namespacedMethods;
    private List<ClassMethod> classMethods;
    private List<String> streams;

    public PlatformMethods(Platform platform, List<NamespacedMethod> namespacedMethods, List<ClassMethod> classMethods, List<String> streams) {
        this.platform = platform;
        this.namespacedMethods = namespacedMethods;
        this.classMethods = classMethods;
        this.streams = streams;
    }

    public PlatformMethods(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public Platform getPlatform() {
        return platform;
    }

    public List<NamespacedMethod> getNamespacedMethods() {
        return namespacedMethods;
    }

    public List<ClassMethod> getClassMethods() {
        return classMethods;
    }

    public List<String> getStreams() {
        return streams;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.platform = new Platform(jsonObject.getJSONObject("platform"));

        JSONArray namespaces = jsonObject.getJSONArray("namespaces");

        this.namespacedMethods = new ArrayList<>();

        for (int i = 0; i < namespaces.length(); i++) {
            this.namespacedMethods.add(new NamespacedMethod(namespaces.getJSONObject(i)));
        }

        JSONArray classes = jsonObject.getJSONArray("classes");

        this.classMethods = new ArrayList<>();

        for (int i = 0; i < classes.length(); i++) {
            this.classMethods.add(new ClassMethod(classes.getJSONObject(i)));
        }

        JSONArray streams = jsonObject.getJSONArray("streams");

        this.streams = new ArrayList<>();

        for (int i = 0; i < streams.length(); i++) {
            this.streams.add(streams.getString(i));
        }
    }
}
