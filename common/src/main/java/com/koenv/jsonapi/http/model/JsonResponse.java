package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.serializer.SerializerManager;
import com.koenv.jsonapi.util.json.JSONObject;

public interface JsonResponse {
    JSONObject toJson(SerializerManager serializerManager);
}
