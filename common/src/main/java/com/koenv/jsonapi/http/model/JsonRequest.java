package com.koenv.jsonapi.http.model;

import com.koenv.jsonapi.methods.ExcludeFromDoc;
import com.koenv.jsonapi.util.json.JSONArray;
import com.koenv.jsonapi.util.json.JSONException;
import com.koenv.jsonapi.util.json.JSONObject;
import com.koenv.jsonapi.util.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

@ExcludeFromDoc
public class JsonRequest {
    private String expression;
    private String tag;

    public JsonRequest(String expression, String tag) {
        this.expression = expression;
        this.tag = tag;
    }

    public String getExpression() {
        return expression;
    }

    public String getTag() {
        return tag;
    }

    public static JsonRequest fromJson(JSONObject json) throws JSONException {
        String expression = json.optString("expression");
        String tag = json.optString("tag");

        return new JsonRequest(expression, tag);
    }

    public static List<JsonRequest> fromJson(String json) throws JSONException {
        List<JsonRequest> requests = new ArrayList<>();
        JSONTokener jsonTokener = new JSONTokener(json);
        Object value = jsonTokener.nextValue();
        if (value instanceof JSONObject) {
            requests.add(fromJson((JSONObject) value));
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.length(); i++) {
                Object object = array.get(i);
                if (object instanceof JSONObject) {
                    requests.add(fromJson((JSONObject) object));
                }
            }
        } else {
            throw new IllegalArgumentException("No JSON object or array found");
        }

        return requests;
    }

    public static JsonErrorResponse getErrorRequest(int code, String message, String tag) {
        return new JsonErrorResponse(code, message, tag);
    }
}
