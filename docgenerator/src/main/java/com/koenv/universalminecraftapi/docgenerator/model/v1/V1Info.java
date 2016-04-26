package com.koenv.universalminecraftapi.docgenerator.model.v1;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class V1Info {
    protected List<NamespacedMethod> namespacedMethods;
    protected List<ClassMethod> classMethods;
    protected List<String> streams;

    public V1Info(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
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
