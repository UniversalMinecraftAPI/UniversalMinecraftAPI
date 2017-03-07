package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONObject;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

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

    public void toJson(JSONWriter writer, SerializerManager serializerManager) {
        writer.object()
                .key("success").value(false)
                .key("code").value(code)
                .key("message").value(message)
                .key("tag").value(tag)
                .endObject();
    }
}
