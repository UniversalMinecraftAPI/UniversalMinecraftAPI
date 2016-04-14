package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

public class JsonErrorResponse implements JsonSerializable {
    private int code;
    private String message;
    private String tag;

    public JsonErrorResponse(int code, String message, String tag) {
        this.code = code;
        this.message = message;
        this.tag = tag;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTag() {
        return tag;
    }

    public JSONObject toJson(SerializerManager serializerManager) {
        JSONObject object = new JSONObject();
        object.put("success", false);
        object.put("code", code);
        object.put("message", message);
        object.put("tag", tag);
        return object;
    }
}
