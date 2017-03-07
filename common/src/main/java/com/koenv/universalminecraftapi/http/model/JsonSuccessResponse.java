package com.koenv.universalminecraftapi.http.model;

import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

public class JsonSuccessResponse implements JsonSerializable {
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

    public void toJson(JSONWriter writer, SerializerManager serializerManager) {
        writer.object()
                .key("success").value(true)
                .key("result");

        serializerManager.serialize(value, writer);

        writer.key("tag").value(tag)
                .endObject();
    }
}
