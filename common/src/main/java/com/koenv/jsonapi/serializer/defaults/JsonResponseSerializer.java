package com.koenv.jsonapi.serializer.defaults;

import com.koenv.jsonapi.http.model.JsonResponse;
import com.koenv.jsonapi.serializer.Serializer;
import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONValue;

public class JsonResponseSerializer implements Serializer<JsonResponse> {
    @Override
    public JSONValue toJson(JsonResponse object, SerializerManager serializerManager) {
        return object.toJson(serializerManager);
    }
}
