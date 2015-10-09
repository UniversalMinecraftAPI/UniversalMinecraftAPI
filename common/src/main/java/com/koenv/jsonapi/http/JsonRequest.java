package com.koenv.jsonapi.http;

import com.koenv.jsonapi.util.json.JSONArray;
import com.koenv.jsonapi.util.json.JSONException;
import com.koenv.jsonapi.util.json.JSONObject;
import com.koenv.jsonapi.util.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class JsonRequest {
    private String name;
    private String key;
    private String username;
    private JSONArray arguments;
    private String tag;

    private String errorMessage;

    public JsonRequest(String name, String key, String username, JSONArray arguments, String tag) {
        this.name = name;
        this.key = key;
        this.username = username;
        this.arguments = arguments;
        this.tag = tag;
    }

    public JsonRequest(String name, String tag, String errorMessage) {
        this.name = name;
        this.tag = tag;
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getUsername() {
        return username;
    }

    public JSONArray getArguments() {
        return arguments;
    }

    public String getTag() {
        return tag;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static JsonRequest fromJson(JSONObject json) throws JSONException {
        String tag = null;
        if (json.has("tag")) {
            tag = json.getString("tag");
        }
        if (!json.has("name")) {
            return getErrorRequest(tag, null, "Please provide a method name for the request.");
        }
        String name = json.getString("name");
        if (!json.has("key")) {
            return getErrorRequest(tag, name, "Please provide an authentication key for the request.");
        }
        String key = json.getString("key");
        if (!json.has("username")) {
            return getErrorRequest(tag, name, "Please provide a username for the request.");
        }
        String username = json.getString("username");

        JSONArray arguments = null;
        if (json.has("arguments")) {
            Object args = json.get("arguments");
            if (args instanceof JSONArray) {
                arguments = (JSONArray) args;
            } else {
                arguments = new JSONArray();
                arguments.put(args);
            }
        }

        if (arguments == null) {
            arguments = new JSONArray();
        }

        return new JsonRequest(name, key, username, arguments, tag);
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
        }

        return requests;
    }

    public static JsonRequest getErrorRequest(String tag, String name, String errorMessage) {
        return new JsonRequest(name, tag, errorMessage);
    }
}
