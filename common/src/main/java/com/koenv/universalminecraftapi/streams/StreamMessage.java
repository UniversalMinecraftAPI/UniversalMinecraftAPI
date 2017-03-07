package com.koenv.universalminecraftapi.streams;

import com.koenv.universalminecraftapi.http.model.JsonSerializable;
import com.koenv.universalminecraftapi.serializer.SerializerManager;
import com.koenv.universalminecraftapi.util.json.JSONWriter;

public class StreamMessage implements JsonSerializable {
    private Object value;
    private String tag;
    private String stream;

    public StreamMessage(Object value, String tag, String stream) {
        this.value = value;
        this.tag = tag;
        this.stream = stream;
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

        writer
                .key("tag").value(tag)
                .key("stream").value(stream)
                .endObject();
    }
}
