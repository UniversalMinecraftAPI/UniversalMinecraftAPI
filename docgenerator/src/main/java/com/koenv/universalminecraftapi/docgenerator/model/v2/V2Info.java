package com.koenv.universalminecraftapi.docgenerator.model.v2;

import com.koenv.universalminecraftapi.util.json.JSONArray;
import com.koenv.universalminecraftapi.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class V2Info {
    protected List<RestResource> resources;
    protected List<RestOperation> operations;

    public V2Info(JSONObject jsonObject) {
        this.populateFrom(jsonObject);
    }

    public List<?> getResources() {
        return resources;
    }

    public List<?> getOperations() {
        return operations;
    }

    public void populateFrom(JSONObject jsonObject) {
        JSONArray resources = jsonObject.getJSONArray("resources");

        this.resources = new ArrayList<>();

        for (int i = 0; i < resources.length(); i++) {
            this.resources.add(new RestResource(resources.getJSONObject(i)));
        }

        JSONArray operations = jsonObject.getJSONArray("operations");

        this.operations = new ArrayList<>();

        for (int i = 0; i < operations.length(); i++) {
            this.operations.add(new RestOperation(operations.getJSONObject(i)));
        }
    }
}
