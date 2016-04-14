package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

public class JsonSuccessResponse implements JsonResponse {
    private Object value;
    private String tag;

    public JsonSuccessResponse(Object value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    public Object getValue() {
        return value;
    }

    public String getTag() {
        return tag;
    }

    public JSONObject toJson(SerializerManager serializerManager) {
        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("result", serializerManager.serialize(value));
        object.put("tag", tag);
        return object;
    }
}
