package com.koenv.universalminecraftapi.docgenerator.model;

import com.koenv.universalminecraftapi.docgenerator.model.v1.V1Info;
import com.koenv.universalminecraftapi.docgenerator.model.v2.V2Info;
import com.koenv.universalminecraftapi.util.json.JSONObject;

public class PlatformMethods {
    protected Platform platform;
    protected V1Info v1;
    protected V2Info v2;

    public PlatformMethods(Platform platform, JSONObject jsonObject) {
        this.platform = platform;
        this.populateFrom(jsonObject);
    }

    public Platform getPlatform() {
        return platform;
    }

    public V1Info getV1() {
        return v1;
    }

    public V2Info getV2() {
        return v2;
    }

    public void populateFrom(JSONObject jsonObject) {
        this.v1 = new V1Info(jsonObject.getJSONObject("v1"));
        this.v2 = new V2Info(jsonObject.getJSONObject("v2"));
    }
}
