package com.koenv.jsonapi.streams;

import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

public class StreamMessage implements JsonResponse {
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

    public JSONObject toJson(SerializerManager serializerManager) {
        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("result", serializerManager.serialize(value));
        object.put("tag", tag);
        object.put("stream", stream);
        return object;
    }
}
